package com.huirong.spark.format

import org.json.{JSONArray, JSONObject}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by huirong on 17-3-13.
  */
object readJson {

  def main(args: Array[String]): Unit ={

    val file = Source.fromFile("/home/huirong/nan.json")
    for (line <- file.getLines()){
      println(line)
      Parse.parse(line)
    }
  }

//  def parse(line: String): JSONObject = {
//    var map: mutable.HashMap[String, ListBuffer[String]] = new mutable.HashMap[String, ListBuffer[String]]()
//    val obj = new JSONObject(line)
//    val rows = obj.getJSONArray("rows")
//    val newObj = new JSONObject()
//    val newArray = new JSONArray()
//    for (index <- 0 to rows.length() - 1){
//      val city = rows.getJSONArray(index)
//      val keyvalue = city.getJSONArray(2);
//      for (keyIndex <- 0 to keyvalue.length() - 1){
//        val key = keyvalue.getJSONArray(keyIndex).getString(0)
//        val value = keyvalue.getJSONArray(keyIndex).getString(1)
//        if (map.contains(key)){
//          val list = map.get(key).get
//          list.+=(value)
//          map.put(key, list)
//        }else{
//          map.put(key, ListBuffer(value))
//        }
//      }
//    }
//    println(map)
//    return newObj;
//  }

}
