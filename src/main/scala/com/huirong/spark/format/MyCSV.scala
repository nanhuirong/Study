//package com.huirong.spark.format
//
//import java.io.StringReader
//
//import au.com.bytecode.opencsv.CSVReader
//import org.apache.spark.{SparkConf, SparkContext}
//
///**
//  * Created by huirong on 17-2-22.
//  */
//object MyCSV {
//  def main(args: Array[String]) = {
//    val conf = new SparkConf().setAppName("MyJson")
//    val sc = new SparkContext(conf)
//    val rdd = sc.textFile("file:///home/huirong/data.csv")
//    val result = rdd.map{record =>
//      val reader: CSVReader = new CSVReader(new StringReader(record))
//      reader.readNext()
//    }
//    result.foreach(split => println(split.mkString(",")))
//    sc.stop()
//
//  }
//
//}
