package com.huirong.spark

import org.apache.spark.{SparkConf, SparkContext}
/**
  * Created by Huirong on 17/1/4.
  */
object HelloWord {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("HelloWord")
    val sc = new SparkContext(conf)
    val data = sc.textFile("alluxio://localhost:19998/README.md")
    println(data.count())

    println("-----------")
    sc.stop()

  }

}
