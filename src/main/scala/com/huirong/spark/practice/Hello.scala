package com.huirong.spark.practice

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by huirong on 17-4-26.
  */
object Hello {
  def main(args: Array[String]): Unit ={
    val conf = new SparkConf().setAppName("南慧荣")
    val sc = new SparkContext(conf)

    //寻找素数
    val n = 2000000
    val composite = sc.parallelize(2 to n, 8)
      .map(x => (x, ( 2 to (n / x))))
      .flatMap(kv => kv._2.map(kv._1 * _))
      //降低数据不均衡的概率，但是会触发一个全局的shuffle
      .repartition(8)
    val prime = sc.parallelize(2 to n, 8).subtract(composite)
    prime.count()

    //spark二次排序
    // 2014-2 64
    // 2015-3 1, 4, 21
    sc.textFile("")
      .map(_.split(","))
      .map(item => (s"${item(0)}-${item(1)}", item(2)))
      .groupByKey()
      .map(item => (item._1, item._2.toList.sortWith(_.toInt < _.toInt)))
      .collect()
      .foreach(item => println(s"${item._1}\t ${item._2.mkString(",")}"))

    /**
      * DataSet 与RDD的区别在于RDD是任何对象的集合，DataSet是特定域的集合
      * DataFrame（在编译时不会对模式进行检查）是特殊的DataSet
      * SparkSession 是Spark2.0引入的API，实际上是SQLContext与HiveContext的组合
      */


  }

}
