package com.huirong.spark.format

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by huirong on 17-2-23.
  */
object MyObject {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("MyObject")
    val sc = new SparkContext(conf)



    sc.stop()
  }

}
