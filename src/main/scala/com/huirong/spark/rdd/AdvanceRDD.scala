package com.huirong.spark.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by huirong on 17-2-23.
  */
object AdvanceRDD {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("AdvanceRDD")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("")
    accumulator(rdd, sc)


    sc.stop()
  }


  def accumulator(rdd: RDD[String], sc: SparkContext):RDD[String] ={
    val blankLine = sc.accumulator(0)
    val callSigns = rdd.flatMap{line =>
      if(line == ""){
        blankLine += 1
      }
      line.split(" ")
    }
    callSigns.count()
    println("BlankLins lines: " + blankLine.value)
    callSigns
  }


  def validateSign(rdd: RDD[String], sc: SparkContext) = {
    val validSignCount = sc.accumulator(0)
    val invalidSignCount = sc.accumulator(0)

  }

//  def isValid(line: String): Boolean = {
//    if(re.match)
//  }

}
