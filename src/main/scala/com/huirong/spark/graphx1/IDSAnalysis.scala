package com.huirong.spark.graphx1

import com.huirong.ids.IDSLog
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
  * Created by huirong on 17-5-8.
  */
object IDSAnalysis {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("IDSAnalysis")
      .setMaster("local[*]")
      .set("SPARK_WORKER_MEMEORY", "5g")
      .set("spark.driver.memory", "5g")
      .set("spark.executor.memory", "5g")
    val sc = new SparkContext(conf)
    val scrape = sc.textFile("file:///home/huirong/graph/ids.log")
    val trans = loadData(sc, scrape)
    var count = trans.count()
    println(s"记录数目${count}")
    trans.map(line => {
      line.toString
    }).saveAsTextFile("file:///home/huirong/graph/idsOut")
    sc.stop()
  }
  def loadData(sc: SparkContext, rdd: RDD[String]): RDD[IDSLog] = {
    val scrape = rdd.map(line => {
      IDSLog.parse(line)
    }).filter(_ != null)
    scrape
  }

}
