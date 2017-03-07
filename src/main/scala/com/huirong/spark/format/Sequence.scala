package com.huirong.spark.format

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text

/**
  * Created by huirong on 17-2-23.
  */
object Sequence {
  def main(args: Array[String]) = {
    val conf = new SparkConf().setAppName("Sequence")
    val sc = new SparkContext(conf)
    val data = sc.sequenceFile("", classOf[Text], classOf[IntWritable]).
      map{case (key, value) => (key.toString, value.get())}
    data.saveAsSequenceFile("")
    sc.stop()
  }

}
