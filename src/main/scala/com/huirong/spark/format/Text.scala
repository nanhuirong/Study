package com.huirong.spark.format

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by huirong on 17-2-22.
  */
object Text {

  def main(args: Array[String]) = {
    val conf = new SparkConf().setAppName("Text")
    val sc = new SparkContext(conf)
    //以一行一条记录的方式读入文件
    val rdd = sc.textFile("PATH")
    //以一个文件一行的方式读入（fileName, Content）
    val other = sc.wholeTextFiles("")

    rdd.saveAsTextFile("")
  }

}
