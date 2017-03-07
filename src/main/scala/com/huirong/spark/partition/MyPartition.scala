package com.huirong.spark.partition

//Spark 存在HashPartitioner 和RangePartition

import java.net.URL

import org.apache.spark.{Partitioner, SparkConf, SparkContext}

/**
  * Created by huirong on 17-2-22.
  */
object MyPartition {
  def main(args: Array[String]) = {
    val conf = new SparkConf().setAppName("MyPartition")
    val sc = new SparkContext(conf)
    val data = sc.parallelize(1 to 1000000, 10)
    //获取分区数目
    println(data.getNumPartitions)
    //返回给定键的分区编号
    data.partitions.foreach(partition => partition.hashCode())
    //返回分区类型
//    data.partitioner

    data.count()
    Thread.sleep(100000)
    sc.stop()

  }


}

class MyDefinePartition(numParts: Int)extends Partitioner{
  override def numPartitions = numParts

  override def getPartition(key: Any): Int = {
    val domain = new URL(key.toString).getHost
    val code = domain.hashCode % numPartitions
    if(code < 0){
      code + numPartitions
    }else{
      code
    }
  }

  override def equals(other: scala.Any): Boolean = other match {
    case dnp: MyDefinePartition => dnp.numPartitions == numPartitions
    case _ => false
  }

}
