package com.huirong.spark.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{Accumulator, SparkConf, SparkContext}

/**
  * Created by huirong on 17-2-23.
  */
object AdvanceRDD {
  private var validSignCount: Accumulator[Int] = _
  private var invalidSignCount: Accumulator[Int] = _

  def main(args: Array[String]) = {
    val conf = new SparkConf().setAppName("AdvanceRDD")
    val sc = new SparkContext(conf)
    validSignCount = sc.accumulator(0)
    invalidSignCount = sc.accumulator(0)
    val rdd = sc.parallelize(List("W8PAL","KK6JKQ", "W6BB", "VE3UOW", "VE2CUA",
      "VE2UN", "OH2TI", "GB1MIR", "K2AMH", "UA1LO", "N7ICE"))
//    accumulator(rdd, sc)

  valid(rdd)
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


  def validateSign(line: String): Boolean = {
    val regex = """\A\d?[a-zA-z]{1,2}\d{1,4}[a-zA-z]{1,3}\Z""".r
    if(line.matches("""\A\d?[a-zA-z]{1,2}\d{1,4}[a-zA-z]{1,3}\Z""")){
      validSignCount += 1
      true
    }else{
      invalidSignCount += 1
      false
    }
  }

  def valid(rdd: RDD[String]) = {
    val validSign = rdd.filter(validateSign)
    validSign.count()
    println(validSignCount.value + "\t" + invalidSignCount.value)
  }

  //数值计算
  def valueCompute(rdd: RDD[Double]) = {
    val status = rdd.stats()
    status.count
    status.mean
    status.sum
    status.max
    status.min
    //方差
    status.variance
    //采样计算方差
    status.sampleVariance
    //标准差
    status.stdev
    //采样计算标准差
    status.sampleStdev
  }



}
