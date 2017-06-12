package com.huirong.spark

import com.huirong.netflow.{NetFlow, NetFlowTool}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
/**
  * Created by Huirong on 17/1/4.
  */

case class Tuple(date: String,
                 sIP: String,
                 sPort: String,
                 dIP: String,
                 dPort: String,
                 protocol: String)
object HelloWord {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("Louvain")
      .setMaster("local[*]")
      .set("spark.driver.memory", "5g")
    val sc = new SparkContext(conf)
//    val path = "file:///home/huirong/graph/netflow/2017-05-08/nfc*"
//    val out = "file:///home/huirong/graph/netflow/2017-05-08-1"
//    val input = loadData(sc, path);





    val path = "file:///home/huirong/graph/netflow/2017-05-08/nfc*"
    val time = "2017-05-08 09:30"
    val out = "file:///home/huirong/matrix1"
//    val data = sc.textFile(path)
//      .map(tuple => {
//        val split = tuple.split(",")
//        val sIP = split(1)
//        val sIPsub = sIP.substring(0, sIP.lastIndexOf("."))
//        val dIP = split(3)
//        val dIPsub = dIP.substring(0, dIP.lastIndexOf("."))
//        (split(0), sIPsub, dIPsub)
//      }).flatMap(line => Array((line._1, line._2), (line._1, line._3)))
//        .groupByKey()
//    data.map(line => (line._1, line._2.size)).sortByKey().foreach(println(_))
//    data.map(line => (line._1, line._2.size)).coalesce(1)
//      .sortByKey(true).map(line => line._1 + "," + line._2)
//      .saveAsTextFile("file:///home/huirong/graph/netflow/out")

    println("------")
//    println(loadData(sc, path).count() + "------")
//    val filter = metrics(sc, path, "2017-05-08 09:30")
//    filter.foreach(line =>  println(line + "------"))
//    filter.coalesce(1).saveAsTextFile("file:///home/huirong/out")
    val data = loadData(sc, path).coalesce(4)
    val graph = buildOneProjection(data,time)
    val result = buildVector(graph)
    result.coalesce(1).saveAsTextFile(out)
//    result.sortBy(_._5).coalesce(1).saveAsTextFile(out)






//   Thread.sleep(1000000)
//    sc.stop()

  }

  def loadData(sc: SparkContext, path: String) = {
    sc.textFile(path)
      .map(NetFlow.parse(_))
      .filter(_ != null)
      .map(line => Tuple(line.getDate ,line.getSrcIp, line.getSrcPort,
        line.getDestIp, line.getDestPort, line.getProtocol))
  }
  def loadData(sc: SparkContext, path: String, date: String) = {
    sc.textFile(path)
      .map(NetFlow.parse(_))
      .filter(_ != null)
      .map(line => Tuple(line.getDate ,line.getSrcIp, line.getSrcPort,
        line.getDestIp, line.getDestPort, line.getProtocol))
  }



  /**
    * 创建二分图的单模投影
    */
  def buildOneProjection(rdd: RDD[Tuple], date: String): RDD[(String, String, Double)] = {
    //过滤出指定时间的数据，并且是从网内向外部发出的链接
    val filter = rdd.filter(tuple => tuple.date.startsWith(date)
      && NetFlowTool.isTJUT(tuple.sIP))
    val recordRDD = filter.map(tuple => (tuple.dIP, tuple.sIP))
      .groupByKey()
      .flatMap(line => line._2.toSet.toArray.sorted.combinations(2))
      .map(line => (line(0), line(1)))
    val metrics = filter.map(tuple => (tuple.sIP, tuple.dIP))
      .groupByKey()
      .collectAsMap()
    val result = recordRDD.map(record => {
      val set1 = metrics(record._1).toSet
      val set2 = metrics(record._2).toSet
      val union = set1 ++ set2
      var count = 0
      for (elem <- set1){
        if (set2.contains(elem)){
          count = count + 1;
        }
      }
      val weight = count.toDouble / union.size.toDouble
//      (record._1, record._2, f"$weight%1.2f")
      (record._1.toString, record._2.toString, f"$weight%1.2f".toDouble)
    })
    result
  }

  def buildVector(rdd: RDD[(String, String, Double)]) ={
    val data = rdd.map(record => (record._1 + "-" + record._2, record._3))
    val local = data.collectAsMap()
    val ips = rdd.flatMap(record => Array(record._1, record._2)).distinct()
    val localIps = ips.collect()
    val matrix = ips.map(line => {
      val arr = new Array[Double](localIps.length)
      for (index <- 0 until localIps.length){
        if (local.contains(line + "-" + localIps(index))){
          arr(index) = local(line + "-" + localIps(index))
        }else{
          arr(index) = 0.0
        }
      }
      (line, arr.mkString("|"))
    })
    matrix
  }

  /**
    * 统计每分钟/24前缀的子网数据规模
    */
  def metrics(sc: SparkContext, path: String, date: String) = {
    val data = loadData(sc, path)
      .filter(tuple => NetFlowTool.isTJUT(tuple.sIP) && tuple.date.startsWith(date))
      .map(tuple => {
        (tuple.sIP.substring(0, tuple.sIP.lastIndexOf(".")), 1)
      }).reduceByKey(_+_)
//    data.collectAsMap().toSeq.sortBy(_._2)
    data.sortBy(_._2)
  }


}
