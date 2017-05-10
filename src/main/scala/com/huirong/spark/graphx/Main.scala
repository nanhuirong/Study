package com.huirong.spark.graphx

import java.io.File

import com.huirong.netflow.NetFlow
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._

/**
  * Created by huirong on 17-5-3.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val path = "file:///home/huirong/graph/netflow/nfcapd.2017042408*"
    val out = "file:///home/huirong/graph/out"
    val conf = new SparkConf()
      .setAppName("Louvain")
      .setMaster("local[*]")
    val sc = new SparkContext(conf)

    var count = 0L
    //读取数据
    val data = loadData(sc, path)
    count = data.count()
    println(s"记录条数${count}")
    val parse = parseIP(data)
    count = parse.count()
    println(s"去重后记录条数${count}")
    val edgeRDD = parse.map(pair => new Edge[Double](pair._1, pair._2, 1.0)).sample(false, 0.001)
    println(s"边数${edgeRDD.count()}")

    //创建图
    val graph = Graph.fromEdges(edgeRDD, None)
    val result = Louvain.execute(sc, graph)
    val file = new File(out)
    if (file.exists()){
      file.delete()
    }
    result.repartition(1).saveAsTextFile(out)
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
  def parseIP(data: RDD[(String, String)]): RDD[(Long, Long)] = {
    data.map{ case (srcIP, destIP) =>
      val src = IpAddress.toLong(srcIP)
      val dest = IpAddress.toLong(destIP)
      if (src > dest){
        (dest, src)
      }else {
        (src, dest)
      }
    }.distinct()
  }

}
