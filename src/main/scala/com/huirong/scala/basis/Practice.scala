package com.huirong.scala.basis


import scala.io.Source
import scala.math._
import scala.sys.process.processInternal.IOException

/**
  * Created by huirong on 17-3-15.
  * 熟悉scala的基础语法
  */
object Practice {

  def main(args: Array[String]) = {
    //从控制台读入数据
    val name = readLine()
    println(name)
    val age = readInt()
    println(age)

    //to 包含[src, dest]
    for (index <- 1 to 10){
      print(index + ",")
    }
    println()
    //until包含[src, dest)
    for (index <- 1 until 10){
      print(index + ",")
    }
    println()

    //高级循环
    for (i <- 1 to 3; j <- 1 to 3){
      print(i * 10 + j + ",")
    }
    println()
    //高级循环, 加守卫
    for (i <- 1 to 3; j <- 1 to 3 if i != j){
      print(i * 10 + j + ",")
    }

    for (i <- 1 to 3; from = 4 - i; j <- from to 3){
      print(i * 10 + j + ",")
    }
    //生成一个Vector集合, yield生成的集合与第一个生成器类型兼容
    for (i <- 1 to 10)yield{
      i % 3
    }
    println()
    println(decorate("nanhuirong"))
    println(decorate("nanhuirong", right = ">>>"))
    println(sum(1, 2, 3, 4, 5))
    //_* 将1 to 5作为一个序类进行处理
    println(sum(1 to 5: _*))

    //懒值
    lazy val words = Source.fromFile("/home/huirong/1.txt").mkString

    //异常处理
    val number = 100
    if (number >= 0){
      Math.sqrt(number)
    }else{
      throw new IllegalArgumentException("number 应该> 0")
    }

    try {
      val file = Source.fromFile("")
    }catch {
      case ex: IOException => ex.printStackTrace()
      case ex: Exception => ex.printStackTrace()
    }finally {
      
    }
  }

  //默认参数和带名参数
  def decorate(str: String, left: String = "[", right: String = "]") = {
    left + str + right
  }

  //变长参数, 其实是一个Seq类型的参数
  def sum(args: Int*) ={
    var result = 0
    for (arg <- args) result += arg
    result
  }

}
