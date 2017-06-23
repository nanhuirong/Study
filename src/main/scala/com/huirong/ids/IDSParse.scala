package com.huirong.ids

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.json.JSONObject

/**
  * Created by huirong on 17-2-23.
  */
object IDSParse {
  def main(args: Array[String]) = {
    val conf = new SparkConf()
      .setAppName("IDSParse")
      .setMaster("local[*]")
      .set("spark.driver.memory", "5g")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("file:///home/huirong/graph/ids.log")
    val scrape = parseJson(rdd, sc)
      .filter(record => record.getTime.startsWith("2017-05-08 "))
      .coalesce(10)
    val count = scrape.count()
    println(s"---${count}")
    scrape.coalesce(1).saveAsTextFile("file:///home/huirong/graph/IDS/")
    sc.stop()


  }

  def parseJson(rdd: RDD[String], sc: SparkContext): RDD[IDSLog] = {
    val idsLog = rdd.map(line => IDSLog.parse(line))
//    println(idsLog.filter(obj => obj ==null).count())
    idsLog.filter(obj => obj != null)
  }


}
