package com.huirong

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Huirong on 17/1/4.
  */
object HelloWord {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("shabi")
    val sc = new SparkContext(conf)
    val data = sc.textFile("file:///home/huirong/work/spark-1.6.1-bin-hadoop2.6/README.md")
    data.count()

    println("-----------")
    sc.stop()
  }

}
