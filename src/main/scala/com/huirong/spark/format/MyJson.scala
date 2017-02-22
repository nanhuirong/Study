package com.huirong.spark.format

import org.apache.spark.{SparkConf, SparkContext}
import org.json.JSONObject


/**
  * Created by huirong on 17-2-22.
  */
object MyJson {
  def main(args: Array[String]) = {
    val conf = new SparkConf().setAppName("MyJson")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("file:///home/huirong/data.json")
    val result = rdd.map{record =>
      val obj = new JSONObject(record)
      val newObj = new JSONObject()
      newObj.put("name", obj.getString("name"))
    }
    result.saveAsTextFile("/home/huirong/json/")

    sc.stop()


  }

}
