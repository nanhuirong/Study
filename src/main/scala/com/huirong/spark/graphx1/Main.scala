package com.huirong.spark.graphx1

import java.io.File

import com.huirong.netflow.{NetFlow, NetFlowTool}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by huirong on 17-5-3.
  */
case class Tuple(date: String,
                 sIP: String,
                 sPort: String,
                 dIP: String,
                 dPort: String,
                 protocol: String)
object Main {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("Louvain")
      .setMaster("local[*]")
      .set("spark.driver.memory", "5g")
    val sc = new SparkContext(conf)

    val inputPath = "file:///home/huirong/graph/tjut/2017-06-12/nfcapd.2017061221*"
//    val inputPath = "file:///home/huirong/graph/tjut/2017-05-08/nfcapd.20170508*"
    val out = "file:///home/huirong/graph/tjut/2017-06-12-src-cluster/"
    var count = 0L
    //读取数据
    val data = loadData(sc, inputPath).coalesce(4)
//    val parse = buildOneProjection_prefix_innerip(data, "59.67.157").map(record => {
//      val ip1 = IpAddress.toLong(record._1)
//      val ip2 = IpAddress.toLong(record._2)
//      if (ip1 < ip2){
//        (ip1, ip2, record._3)
//      }else{
//        (ip2, ip1, record._3)
//      }
//    })
//    count = parse.count()
//    println(s"node---${count}")
//    val edgeRDD = parse.map(pair => new Edge[Long](pair._1, pair._2, pair._3))
//    if (count > 10){
//      //创建图
//      val graph = Graph.fromEdges(edgeRDD, None)
//      val minProgress = 1
//      val progressCounter = 1
//      val runner = new HDFSLouvainRunner(minProgress, progressCounter,
//        out + "total")
//      runner.run(sc, graph)
//    }
    val timeFrame = extractTimeFrame(data)
    timeFrame.foreach(line => println("------" + line))
    for (time <- timeFrame){
      /**
        * 202.113.215
        *
        * tjut
        * d 59.67.153
        * d/s 202.113.70
        * d/s 202.113.76
        * 59.67.147
        * 59.67.146
        * 59.67.151
        * 59.67.153 最大
        * 59.67.154
        * 59.67.155
        * 59.67.156
        * 59.67.157
        */
      val parse = buildOneProjection_prefix_innerip(data, time, "59.67.157").map(record => {
        val ip1 = IpAddress.toLong(record._1)
        val ip2 = IpAddress.toLong(record._2)
        if (ip1 < ip2){
          (ip1, ip2, record._3)
        }else{
          (ip2, ip1, record._3)
        }
      })
//      val parse = buildOneProjection_prefix_innerip(data, "59.67.153").map(record => {
//        val ip1 = IpAddress.toLong(record._1)
//        val ip2 = IpAddress.toLong(record._2)
//        if (ip1 < ip2){
//          (ip1, ip2, record._3)
//        }else{
//          (ip2, ip1, record._3)
//        }
//      })
      count = parse.count()
      println(s"node---${count}")
      val edgeRDD = parse.map(pair => new Edge[Long](pair._1, pair._2, pair._3))
      if (count > 10){
        //创建图
        val graph = Graph.fromEdges(edgeRDD, None)
        val minProgress = 1
        val progressCounter = 1
        val runner = new HDFSLouvainRunner(minProgress, progressCounter,
          out + time.split(" ")(1).replaceAll(":", "-"))
        runner.run(sc, graph)
      }
    }
//    sc.stop()
  }

  /**
    * 预处理数据
    */
  def loadData(sc: SparkContext, path: String) = {
    sc.textFile(path)
      .map(NetFlow.parse(_))
      .filter(_ != null)
      .map(line => Tuple(line.getDate.substring(0, line.getDate.length - 2) + "00" ,
        line.getSrcIp, line.getSrcPort,
        line.getDestIp, line.getDestPort, line.getProtocol))
  }

  /**
    * 提取时间
    */
  def extractTimeFrame(rdd: RDD[Tuple]) = {
    rdd.map(_.date).distinct().collect()
  }

  /**
    * 创建二分图的单模投影
    */
  def buildOneProjection(rdd: RDD[Tuple], date: String): RDD[(String, String, Long)] = {
    //过滤出指定时间的数据，并且是从网内向外部发出的链接
    val filter = rdd.filter(tuple => tuple.date.startsWith(date)
      && NetFlowTool.isTJUT(tuple.sIP))
//      && (tuple.dPort.equals("80") || tuple.sPort.equals("80")))
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
          count = count + 1
        }
      }
      val weight = count.toDouble / union.size.toDouble
      //      (record._1, record._2, f"$weight%1.2f")
      (record._1.toString, record._2.toString, count.toLong)
    })
    result
  }

  /**
    * 创建源ip的二分图模型
    */
  def buildOneProjection_prefix_sip(rdd: RDD[Tuple], date: String, prefix: String)
    : RDD[(String, String, Long)] = {
    //过滤出指定时间的数据，并且是指定网段前缀的
    val filter = rdd.filter(tuple => tuple.date.startsWith(date)
      && tuple.sIP.startsWith(prefix))
    //      && (tuple.dPort.equals("80") || tuple.sPort.equals("80")))
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
          count = count + 1
        }
      }
//      val weight = count.toDouble / union.size.toDouble
      //      (record._1, record._2, f"$weight%1.2f")
      (record._1.toString, record._2.toString, count.toLong)
    })
//    result.filter(_._3 > 5)
    result
  }

  /**
    * 构建内网对应的外网的二分图模型
    */
  def buildOneProjection_prefix_innerip(rdd: RDD[Tuple], date: String, prefix: String)
  : RDD[(String, String, Long)] = {
    //过滤出指定时间的数据，并且是指定网段前缀的
    val filter = rdd.map(tuple => {
      if (NetFlowTool.isTJUT(tuple.dIP)){
        Tuple(tuple.date, tuple.dIP, tuple.dPort, tuple.sIP, tuple.sPort, tuple.protocol)
      }else{
        tuple
      }
    }).filter(tuple => tuple.date.startsWith(date)
      && tuple.sIP.startsWith(prefix))
    //      && (tuple.dPort.equals("80") || tuple.sPort.equals("80")))
    val recordRDD = filter.map(tuple => (tuple.sIP, tuple.dIP))
      .groupByKey()
      .flatMap(line => line._2.toSet.toArray.sorted.combinations(2))
      .map(line => (line(0), line(1)))
    val metrics = filter.map(tuple => (tuple.dIP, tuple.sIP))
      .groupByKey()
      .collectAsMap()
    val result = recordRDD.map(record => {
      val set1 = metrics(record._1).toSet
      val set2 = metrics(record._2).toSet
      val union = set1 ++ set2
      var count = 0
      for (elem <- set1){
        if (set2.contains(elem)){
          count = count + 1
        }
      }
      //      val weight = count.toDouble / union.size.toDouble
      //      (record._1, record._2, f"$weight%1.2f")
      (record._1.toString, record._2.toString, count.toLong)
    })
    //    result.filter(_._3 > 5)
    result
  }

  def buildOneProjection_prefix_innerip(rdd: RDD[Tuple], prefix: String)
  : RDD[(String, String, Long)] = {
    //过滤出指定时间的数据，并且是指定网段前缀的
    val filter = rdd.map(tuple => {
      if (NetFlowTool.isTJUT(tuple.dIP)){
        Tuple(tuple.date, tuple.dIP, tuple.dPort, tuple.sIP, tuple.sPort, tuple.protocol)
      }else{
        tuple
      }
    }).filter(tuple => tuple.sIP.startsWith(prefix))
    //      && (tuple.dPort.equals("80") || tuple.sPort.equals("80")))
    val recordRDD = filter.map(tuple => (tuple.sIP, tuple.dIP))
      .groupByKey()
      .flatMap(line => line._2.toSet.toArray.sorted.combinations(2))
      .map(line => (line(0), line(1)))
    val metrics = filter.map(tuple => (tuple.dIP, tuple.sIP))
      .groupByKey()
      .collectAsMap()
    val result = recordRDD.map(record => {
      val set1 = metrics(record._1).toSet
      val set2 = metrics(record._2).toSet
//      val union = set1 ++ set2
      var count = 0
      for (elem <- set1){
        if (set2.contains(elem)){
          count = count + 1
        }
      }
      //      val weight = count.toDouble / union.size.toDouble
      //      (record._1, record._2, f"$weight%1.2f")
      (record._1.toString, record._2.toString, count.toLong)
    })
    result.filter(_._3 > 5)
    result
  }

  /**
    * 构建目的ip的二分图
    */
  def buildOneProjection_prefix_dip(rdd: RDD[Tuple], date: String, prefix: String)
  : RDD[(String, String, Long)] = {
    //过滤出指定时间的数据，并且是指定网段前缀的
    val filter = rdd.filter(tuple => tuple.date.startsWith(date)
      && tuple.dIP.startsWith(prefix))
    //      && (tuple.dPort.equals("80") || tuple.sPort.equals("80")))
    val recordRDD = filter.map(tuple => (tuple.sIP, tuple.dIP))
      .groupByKey()
      .flatMap(line => line._2.toSet.toArray.sorted.combinations(2))
      .map(line => (line(0), line(1)))
    val metrics = filter.map(tuple => (tuple.dIP, tuple.sIP))
      .groupByKey()
      .collectAsMap()
    val result = recordRDD.map(record => {
      val set1 = metrics(record._1).toSet
      val set2 = metrics(record._2).toSet
//      val union = set1 ++ set2
      var count = 0
      for (elem <- set1){
        if (set2.contains(elem)){
          count = count + 1
        }
      }
      //      val weight = count.toDouble / union.size.toDouble
      //      (record._1, record._2, f"$weight%1.2f")
      (record._1.toString, record._2.toString, count.toLong)
    })
//        result.filter(_._3 > 5)
    result
  }


  def buildOneProjection_1(rdd: RDD[Tuple]): RDD[(String, String, Long)] = {
    //过滤出指定时间的数据，并且是从网内向外部发出的链接
//    val filter = rdd.filter(tuple => NetFlowTool.isEDU(tuple.sIP) || NetFlowTool
//      && (tuple.sPort.equals("53") || tuple.dPort.equals("53")))
    val filter = rdd.filter(tuple => tuple.sPort.equals("53") || tuple.dPort.equals("53"))
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
          count = count + 1
        }
      }
      val weight = count.toDouble / union.size.toDouble
      //      (record._1, record._2, f"$weight%1.2f")
      (record._1.toString, record._2.toString, count.toLong)
    })
    result
  }


  //将IP转换成对应的Long，并且保证对应大小关系，并且去重
  def parseIP(data: RDD[(String, String)]): RDD[(Long, Long, Long)] = {
    val trans = data.map{ case (srcIP, destIP) =>
      val src = IpAddress.toLong(srcIP)
      val dest = IpAddress.toLong(destIP)
      if (src > dest){
        ((dest, src), 1L)
      }else {
        ((src, dest), 1L)
      }
    }
    trans.reduceByKey(_ + _).map(line => (line._1._1, line._1._2, line._2))
  }

  //构建二元伴生关系
  def parseIP1(data: RDD[(String, String)]): RDD[(Long, Long)] = {
    data.map{ case (srcIP, destIP) =>
      val src = IpAddress.toLong(srcIP)
      val dest = IpAddress.toLong(destIP)
      (dest, src)
    }.groupByKey().flatMap( line => {
      line._2.toSeq.sorted.combinations(2)
    }).map(pair => {
      if (pair(0) < pair(1))
        (pair(0), pair(1))
      else
        (pair(1), pair(0))
    }).distinct()
  }

  //构建无线网络的伴生关系
  def parseIP2(data: RDD[(String, String)]): RDD[(Long, Long, Long)] = {
    val filter = data.filter(line => {
      var flag = false
      if (line._1.startsWith("172.20") || line._2.startsWith("172.20"))
        flag = true
      flag
    }).map(line => {
      if(line._1.startsWith("172.20"))
        (line._1, line._2)
      else
        (line._2, line._1)
    })
    val trans = filter.map((_, 1L))
      .reduceByKey(_+_)
      .map(line => {
        (line._1._1, (line._1._2, line._2))
      }).groupByKey()
        .flatMap(line =>{
          line._2.toSeq.combinations(2)
        }).map(line => {
      val src = IpAddress.toLong(line(0)._1)
      val dest = IpAddress.toLong(line(1)._1)
      val weight = line(0)._2 + line(1)._2
      if (src < dest){
        (src, dest, weight)
      }else{
        (dest, src, weight)
      }
    })
    trans
  }

}
