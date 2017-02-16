package com.huirong

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Huirong on 17/1/4.
  */
object HelloWord {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    val sc = new SparkContext(conf)
    val data = sc.textFile("file:///home/huirong/work/spark-1.5.2-bin-hadoop2.6/README.md")
    data.count()

    println("-----------")
    sc.stop()
  }

}
