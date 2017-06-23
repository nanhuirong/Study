package com.huirong.spark.hbase

import com.huirong.netflow.NetFlow
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by huirong on 17-6-19.
  */
case class Tuple(date: String,
                 sIP: String,
                 sPort: String,
                 dIP: String,
                 dPort: String,
                 protocol: String)
object MakeData {
  val PATH = "file:///home/huirong/graph/edu/2017-05-15/nfc*"
  val OUT = "file:///home/huirong/result/"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("Louvain")
      .setMaster("local[*]")
      .set("spark.driver.memory", "5g")
    val sc = new SparkContext(conf)
    val input = loadData(PATH, sc)
    val data = input.filter(record => isDNSOrHTTP(record))
//    data.map(tuple => tuple.date + "-" + tuple.sIP + "-" + tuple.dIP + "," + tuple.date + "," + tuple.sIP + ","
//      + tuple.sPort + "," + tuple.dIP + "," + tuple.dPort + "," + tuple.protocol)
//      .coalesce(1).saveAsTextFile(OUT + "netflow")
//    val metrics =
    metrics(PATH, sc)


  }

  def loadData(path: String, sc: SparkContext) = {
    val data = sc.textFile(path)
      .map(NetFlow.parse(_))
      .filter(_ != null)
      .map(line => Tuple(line.getDate.substring(0, line.getDate.length - 2) + "00" ,
        line.getSrcIp, line.getSrcPort,
        line.getDestIp, line.getDestPort, line.getProtocol))
    data
  }

  /**
    * 判断是否是HTTP服务或者是DNS服务
    */
  def isDNSOrHTTP(record: Tuple): Boolean = {
    if (record.sPort.equals("80") || record.sPort.equals("53") || record.dPort.equals("80") || record.dPort.equals("53")){
      true
    }else{
      false
    }
  }

  def isDNS(record: NetFlow): Boolean = {
    if (record.getSrcPort.equals("53") || record.getDestPort.equals("53")){
      true
    }else{
      false
    }
  }

  def metrics(path: String, sc: SparkContext) = {
    val input = sc.textFile(path).map(NetFlow.parse(_)).filter(_ != null).filter(isDNS(_))
    val res = input.map(record => {
      val date = record.getDate.substring(0, record.getDate.length - 2) + "00"
      val bytes = record.getBytes
      val packs = record.getPackets
      (date, (1, bytes, packs))
    }).reduceByKey((line1, line2) => (line1._1 + line2._1, line1._2 + line2._2, line1._3 + line2._3))
    res.map(line => line._1 + "-" + "53" + "," + line._2._1 + "," + line._2._2 + "," + line._2._3)
      .coalesce(1)
      .saveAsTextFile(OUT + "metrics")

  }

}
