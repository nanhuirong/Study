package com.huirong.spark.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.Map

/**
  * Created by huirong on 17-2-22.
  *整理RDD操作并深入理解
  */
object MyRDD {

  def main(args: Array[String]) = {
    val conf = new SparkConf().setAppName("myRDD")
    val sc = new SparkContext(conf)

    val src = sc.parallelize(List("coffee", "coffee", "panda", "monkey", "tea"))
    val dest = sc.parallelize(List("coffee", "monkey", "kitty"))
    val pair: RDD[(Long, Long)] = sc.parallelize(List((1, 2), (3, 4), (3, 6), (11, 2), (22, 2)))
    val other: RDD[(Long, Long)] = sc.parallelize(List((3, 9)))

//    union(src, dest)
//    intersection(src, dest)
//    subtract(src,dest)
//    cartesian(src, dest)
//    sample(src)
//    aggregate(sc)
//    countByValue(src)
//    takeOrdered(src)
//    fold(sc)
//    reduceByKey(pair)
//    groupByKey(pair)
//    mapValues(pair)
//    flatMapValues(pair)
//    keys(pair)
//    values(pair)
//    sortByKey(pair)
//    join(pair, other)
//    leftOutJoin(pair, other)
//    cogroup(pair, other)
//    subtractByKey(pair, other)
//    combineByKey(pair)
    //获取RDD分区大小
//    println(src.partitions.size)
    mySortByKey(pair)
    Thread.sleep(100000)
    sc.stop()

  }

  //返回两个RDD中出现的元素, 如果RDD中有重复的元素, 也会返回重复的元素
  def union(src: RDD[String], dest: RDD[String]): RDD[String] = {
    val result = src.union(dest)
    result.foreach(println)
    result
  }

  //返回两个RDD交集, 同时去掉重复元素
  def intersection(src: RDD[String], dest: RDD[String]): RDD[String] = {
    val result = src.intersection(dest)
    result.foreach(println)
    result
  }

  //返回第一个RDD存在但不存在与第二个RDD的元素
  def subtract(src: RDD[String], dest: RDD[String]): RDD[String] = {
    val result = src.subtract(dest)
    result.foreach(println)
    result
  }
  //笛卡尔积, 返回所有可能的配对信息
  def cartesian(src: RDD[String], dest: RDD[String]): RDD[(String, String)] = {
    val result = src.cartesian(dest)
    result.foreach(println)
    result
  }

  //对RDD采样, takeSample会把采样数据集返回Driver
  def sample(rdd: RDD[String]) ={
    //true为放回抽样, false 为不放回抽样
    //比列
    //种子, 保证每次的sample 相同
    val sample = rdd.sample(true, 1.0)
    sample.foreach(println)
  }

  //reduce 与 aggregate
  //reduce 返回结果相同
  //aggregate 返回结果不一定相同
  def aggregate(sc: SparkContext) = {
    val data = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8, 9))
    //求取平均值
    val avg = data.aggregate((0, 0))((acc, value) =>
      (acc._1 + value, acc._2 + 1),
      (acc1, acc2) => (acc1._1 + acc2._1, acc1._2 + acc2._2))
    println(avg._1 / avg._2)
  }

  //fold 与reduce类似, 但是指定一个初始值,
  def fold(sc: SparkContext) = {
    val data = sc.parallelize((List(1, 2, 3, 4, 5)), 10)
    var result = data.fold(0)(_ + _)
    println(result)
    result = data.fold(2)(_ + _)
    println(result)
  }

  //foreach 触发所有data执行action, 不需要将数据将数据返回给Driver
  //countByValue  返回各元素在RDD出现的次数
  def countByValue(rdd: RDD[String]): Map[String, Long] = {
    val result = rdd.countByValue()
    result.foreach(println)
    result
  }

  //take(n) 返回RDD中的N个元素
  //top(n) 返回最前面的N个元素
  //takeOrdered(num)(ordering) 返回排序的n个元素 默认降序
  def takeOrdered(rdd: RDD[String]) {
    val result = rdd.takeOrdered(10)(myOrder)
    result.foreach(println)
    println(result)
  }
  //指定排序规则
  val myOrder = Ordering[String].reverse

  def reduceByKey(rdd: RDD[(Long, Long)]): RDD[(Long, Long)] = {
    val result = rdd.reduceByKey(_ + _)
    result.foreach(println)
    result
  }

  def groupByKey(rdd: RDD[(Long, Long)]): RDD[(Long, Iterable[Long])] = {
    val result = rdd.groupByKey()
    result.foreach{ line =>
      val key: Long = line._1
      val iterator: Iterable[Long]= line._2
      print(key + "\t")
      iterator.foreach(elem => print(elem + ","))
      println()
    }
    result
  }

  def mapValues(rdd: RDD[(Long, Long)]): RDD[(Long, Long)] = {
    val result = rdd.mapValues( _ + 1)
    result.foreach(println)
    result
  }

  def flatMapValues(rdd: RDD[(Long, Long)]): RDD[(Long, Long)] = {
    val result = rdd.flatMapValues{ num =>
      num to 10
    }
    result.foreach(println)
    result
  }

  def keys(rdd: RDD[(Long, Long)]): RDD[(Long)] = {
    val result = rdd.keys
    result.foreach(println)
    result
  }
  def values(rdd: RDD[(Long, Long)]): RDD[Long] = {
    val result = rdd.values
    result.foreach(println)
    result
  }

  //true升序  false降序
  def sortByKey(rdd: RDD[(Long, Long)]): RDD[(Long, Long)] = {
    val result = rdd.sortByKey(false)
    result.foreach(println)
    result
  }
  def mySortByKey(rdd: RDD[(Long, Long)]) ={
    val result = rdd.sortByKey()
    result.foreach(println)
  }
  //按照字符串的排序对数字进行陪需
  implicit val sortLongByString = new Ordering[Long] {
    override def compare(x: Long, y: Long) = x.toString.compare(y.toString)
  }

  //key的类型必须形同
  def join(rdd: RDD[(Long, Long)], oterRDD: RDD[(Long, Long)]): RDD[(Long, (Long, Long))] = {
    val result = rdd.join(oterRDD)
    result.foreach(println)
    result
  }

  //第一个RDD的key必须存在  rightOutJoin与此类似
  def leftOutJoin(rdd: RDD[(Long, Long)], oterRDD: RDD[(Long, Long)]): RDD[(Long, (Long, Option[Long]))] = {
    val result = rdd.leftOuterJoin(oterRDD)
    result.foreach(println)
    result
  }

  //将RDD中具有相同key的数据分组
  def cogroup(rdd: RDD[(Long, Long)], oterRDD: RDD[(Long, Long)]): RDD[(Long, (Iterable[Long], Iterable[Long]))] = {
    val result = rdd.cogroup(oterRDD)
    result.foreach(println)
    result
  }
  //删除RDD中与其他RDDKey相同的元素
  def subtractByKey(rdd: RDD[(Long, Long)], oterRDD: RDD[(Long, Long)]): RDD[(Long, Long)] = {
    val result = rdd.subtractByKey(oterRDD)
    result.foreach(println)
    result
  }

  //foldByKey reduceByKey

  def combineByKey(rdd: RDD[(Long, Long)]): RDD[(Long, Double)] = {
    val result = rdd.combineByKey(
      (v) => (v, 1L),
      (acc: (Long, Long), v) => (acc._1 + v, acc._2 + 1),
      (acc1: (Long, Long), acc2: (Long, Long)) => (acc1._1 + acc2._1, acc1._2+ acc2._2)
    ).map(record => (record._1, record._2._1 / record._2._2.toDouble))
    result.foreach(println)
    result
  }
  //pairRDD特有
  //countByKey() collectAsMap()  lookup(key)



}




