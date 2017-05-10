package com.huirong.spark.graphx1

import java.io.File

import com.huirong.netflow.NetFlow
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by huirong on 17-5-3.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val path = "file:///home/huirong/graph/netflow/2017-05-08/nfc*"
    val out = "file:///home/huirong/graph/out"
    val file = new File(out)
    if (file.exists()){
      file.delete()
    }
    val conf = new SparkConf()
      .setAppName("Louvain")
      .setMaster("local[*]")
      .set("spark.driver.memory", "5g")
    val sc = new SparkContext(conf)

    var count = 0L
    //读取数据
    val data = loadData(sc, path)
    count = data.count()
    println(s"记录条数${count}")
    val parse = parseIP(data)
    count = parse.count()
    println(s"去重后记录条数${count}")
    val edgeRDD = parse.map(pair => new Edge[Long](pair._1, pair._2, pair._3))
    println(s"边数${edgeRDD.count()}")
//    val parse = parseIP2(data)
////    val sample = parse.take(10)
////    sample.foreach(line => println(s"sample${line._1} ${line._2} ${line._3}"))
//    parse.map(line => {
//      (IpAddress.toString(line._1), IpAddress.toString(line._2), line._3)
//    }).sortBy(_._3, true).repartition(1)
//      .saveAsTextFile("file:///home/huirong/graph/out2")


    //创建图
    val graph = Graph.fromEdges(edgeRDD, None)
    val minProgress = 1
    val progressCounter = 1
    val runner = new HDFSLouvainRunner(minProgress, progressCounter, out)
    runner.run(sc, graph)

//    val data1 = sc.textFile("file:///home/huirong/graph/out/level_0_vertices/part-00000")
//    val split = data1.map(_.split(","))
//    val res = split.map(line => (line(1), line(0))).groupByKey()
//      .map(comm => {
//        val len = comm._2.size
//        (comm._1, len)
//      })
//    val order = Ordering.by[(String, Int), Int](_._2)
//    res.top(10)(order).foreach(println)




    sc.stop()
  }

  //读取数据
  def loadData(sc: SparkContext, path: String): RDD[(String, String)] = {
    sc.textFile(path)
      .map(NetFlow.parse(_))
        .filter(_ != null)
      .map(line => (line.getSrcIp, line.getDestIp))
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
