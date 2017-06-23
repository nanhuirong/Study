package com.huirong.spark.graphx1.metrics

import com.huirong.netflow.{NetFlow, NetFlowTool}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by huirong on 17-6-8.
  */
case class Tuple(date: String,
                 sIP: String,
                 sPort: String,
                 dIP: String,
                 dPort: String,
                 protocol: String)
object Metrics {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("Louvain")
      .setMaster("local[*]")
      .set("spark.driver.memory", "5g")
    val sc = new SparkContext(conf)

    val inputPath = "file:///home/huirong/graph/tjut/2017-06-12"
    val input = loadData(sc, inputPath)
    val metricsPath = "file:///home/huirong/graph/tjut/"
//    metricsPrefix(input, metricsPath)
//    metricsPrefixNum(input, metricsPath)
    metricsPrefixNum_1(input, metricsPath)
    sc.stop()
  }

  def loadData(sc: SparkContext, path: String) = {
    sc.textFile(path).coalesce(4)
      .map(NetFlow.parse(_))
      .filter(_ != null)
      .map(line => Tuple(line.getDate ,line.getSrcIp, line.getSrcPort,
        line.getDestIp, line.getDestPort, line.getProtocol))
  }

  /**
    * 按照分钟统计源目的/24位前缀的指标
    * @param rdd
    * @param outPath
    */
  def metricsPrefix(rdd: RDD[Tuple], outPath: String) = {
    val input = rdd.flatMap(tuple => {
      val date = tuple.date.substring(0, tuple.date.length - 2) + "00"
      val sipPrefix = tuple.sIP.substring(0, tuple.sIP.lastIndexOf("."))
      val dipPrefix = tuple.dIP.substring(0, tuple.dIP.lastIndexOf("."))
      Array((date, sipPrefix), (date, dipPrefix))
    })
    val reduce = input.map(line => (line, 1)).reduceByKey(_+_).filter(_._2 > 10)

    reduce.sortBy(_._1)
      .map(line => line._1._1 + "," + line._1._2 + "," + line._2)
      .coalesce(1)
      .saveAsTextFile(outPath + "metrics1")
    reduce.map(line => (line._1))
      .groupByKey()
      .map(line => {
        val count = line._2.toSet.size
        (line._1, count)
      }).sortBy(_._1)
      .map(line => line._1 + "," + line._2)
      .coalesce(1)
      .saveAsTextFile(outPath + "metrics2")

//    input.groupByKey().map(line => {
//      val count = line._2.toSet.size
//      (line._1, count)
//    }).sortBy(_._1)
//      .map(line => line._1 + "," + line._2)
//      .coalesce(1)
//      .saveAsTextFile(outPath + "metrics2")
  }

  /**
    * 按照分钟统计一天中内网、外网/24位前缀的子网数目
    */
  def metricsPrefixNum(rdd: RDD[Tuple], outPath: String) = {
    val input = rdd.flatMap(tuple => {
      val date = tuple.date.substring(0, tuple.date.length - 2) + "00"
      val sipPrefix = tuple.sIP.substring(0, tuple.sIP.lastIndexOf("."))
      val dipPrefix = tuple.dIP.substring(0, tuple.dIP.lastIndexOf("."))
      Array((date, sipPrefix), (date, dipPrefix))
    })
    val result = input.groupByKey()
      .map(group => {
        val set = group._2.toSet
        var inCount = 0
        var outCount = 0
        for (ip <- set){
          if (NetFlowTool.isTJUT(ip)){
            inCount += 1
          }else{
            outCount += 1
          }
        }
        (group._1, (inCount, outCount))
      })
    result.sortBy(_._1)
      .map(line => line._1 + "," + line._2._1 + "," + line._2._2)
      .coalesce(1)
      .saveAsTextFile(outPath + "metricsPrefixNum")
//    result.coalesce(1).saveAsTextFile(outPath + "metricsPrefixNum")

  }
  def metricsPrefixNum_1(rdd: RDD[Tuple], outPath: String) = {
//    val input = rdd.flatMap(tuple => {
//      val date = tuple.date.substring(0, tuple.date.length - 2) + "00"
//      val sipPrefix = tuple.sIP.substring(0, tuple.sIP.lastIndexOf("."))
//      val dipPrefix = tuple.dIP.substring(0, tuple.dIP.lastIndexOf("."))
//      Array((date, sipPrefix), (date, dipPrefix))
//    })
//    val result = input.groupByKey()
//      .map(group => {
//        val set = group._2.toSet
//        var inCount = 0
//        var outCount = 0
//        for (ip <- set){
//          if (NetFlowTool.isTJUT(ip)){
//            inCount += 1
//          }else{
//            outCount += 1
//          }
//        }
//        (group._1, (inCount, outCount))
//      })
//    result.sortBy(_._1)
//      .map(line => line._1 + "," + line._2._1 + "," + line._2._2)
//      .coalesce(1)
//      .saveAsTextFile(outPath + "metricsPrefixNum")
//    //    result.coalesce(1).saveAsTextFile(outPath + "metricsPrefixNum")
    val input = rdd.map(tuple => {
      val time = tuple.date.substring(0, tuple.date.length - 2) + "00"
      if (NetFlowTool.isTJUT(tuple.sIP)){
        val prefix = tuple.sIP.substring(0, tuple.sIP.lastIndexOf("."))
        (time, prefix, tuple.dIP)
      }else{
        val prefix = tuple.dIP.substring(0, tuple.dIP.lastIndexOf("."))
        (time, prefix, tuple.sIP)
      }
    })
    val inPrefix = input.map(pair => (pair._1, pair._2))
      .groupByKey()
      .map(pair => {
        (pair._1, pair._2.toSet.size)
      })
    val outNum = input.map(pair => (pair._1, pair._3))
      .groupByKey()
      .map(pair => {
        (pair._1, pair._2.toSet.size)
      })
    val result = inPrefix.join(outNum)
    result.sortByKey().map(pair => pair._1 + "," + pair._2._1 + "," + pair._2._2)
      .coalesce(1)
      .saveAsTextFile(outPath + "shabi")

  }



}
