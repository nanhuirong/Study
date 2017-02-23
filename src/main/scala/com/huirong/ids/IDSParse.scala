package com.huirong.ids

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.json.JSONObject

/**
  * Created by huirong on 17-2-23.
  */
object IDSParse {
  def main(args: Array[String]) = {
    val conf = new SparkConf().setAppName("IDSParse")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("file:///home/huirong/ids.log")
    val scrape = parseJson(rdd, sc)
    scrape.saveAsTextFile("file:///home/huirong/IDS/")
    sc.stop()


  }

  def parseJson(rdd: RDD[String], sc: SparkContext): RDD[IDSLog] = {
    val idsLog = rdd.map(line => IDSLog.parse(line))
//    println(idsLog.filter(obj => obj ==null).count())
    idsLog.filter(obj => obj != null)
  }


}
