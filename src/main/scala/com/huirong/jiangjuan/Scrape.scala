package com.huirong.jiangjuan

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by huirong on 17-3-14.
  */
object Scrape {
  val PATH = "file:///home/huirong/shabi/Posts.xml"
  val FILWORD = Array("<?xml", "<posts>", "</posts>")
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("Scrape")
      .set("spark.driver.memory", "6g")
    val sc = new SparkContext(conf)
    val data = sc.textFile(PATH)
    val filter = data.filter(line => !(line.startsWith(FILWORD(0)) || line.startsWith(FILWORD(1)) || line.startsWith(FILWORD(2))))
    filter.persist()
    val result = filter.map(Parse.extract(_)).filter(_.length > 0)
    result.coalesce(100).saveAsTextFile("file:///home/huirong/shabi/out")

    sc.stop()




  }

}
