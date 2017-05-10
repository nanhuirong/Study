package com.huirong.spark.graphx1

import com.huirong.netflow.NetFlow
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

/**
  * Created by huirong on 17-5-7.
  */
object Prac {
  def main(args: Array[String]): Unit = {
    val scrape = "file:///home/huirong/graph/netflow/2017042420*"
    val trans = "file:///home/huirong/graph/out/level_final_vertices/part-00000"
    val conf = new SparkConf()
      .setAppName("Louvain")
      .setMaster("local[*]")
    val sc = new SparkContext(conf)
//    val scrapeData = loadData(sc, scrape).collect().toSet
//    println(scrapeData.size + "shabi")
    val transData = sc.textFile(trans).map(line => {
      val split = line.split(",")
      (split(1), split(0))
    }).groupByKey()
      .filter(_._2.size >= 100)
    val count = transData.count()
    println(count + "shabi")
    val res = transData.map(_._2.mkString(","))

//    val res = transData.map(line => {
//      val iteratble = line._2
//      val map = new mutable.HashMap[String, Int]()
//      for (ip <- iteratble){
//        for (src <- scrapeData){
//          if (src._1.equals(ip)){
//            if (map.contains(src._2)){
//              map(src._2) += 1
//            }else{
//              map(src._2) = 1
//            }
//          }else if (src._2.equals(ip)){
//            if (map.contains(src._1)){
//              map(src._1) += 1
//            }else{
//              map(src._1) = 1
//            }
//          }
//        }
//      }
//      (line._1, iteratble.size, map.toSeq.sortBy(_._2).reverse.take(10))
//    })
    res.repartition(1).saveAsTextFile("file:///home/huirong/graph/wanOut")

//    val wan: RDD[(String, String)] = loadData(sc, "file:///home/huirong/graph/netflow/nfcapd*")
//    var count = wan.count()
//    println(s"记录条数${count}")
//    count = wan.filter(line  => {
//      var flag: Boolean = false
//      if(line._1.startsWith("172.20") || line._2.startsWith("172.20")){
//        flag = true
//      }
//      flag
//    }).count
//    println(s"记录条数${count}")

//    val join = scrapeData.join(transData).map(line => {
//      (line._2._2, (line._))
//    })

  }

  def loadData(sc: SparkContext, path: String): RDD[(String, String)] = {
    sc.textFile(path)
      .repartition(4)
      .map(NetFlow.parse(_))
      .filter(_ != null)
      .map(line => (line.getSrcIp, line.getDestIp))
  }

}
