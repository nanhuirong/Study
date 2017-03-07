package com.huirong.scala

/**
  * Created by huirong on 17-3-6.
  */
object MyFac {
  def main(args: Array[String]) = {

  }

  def fact(base: Int): Int = {
    if (base <= 0)
      1
    else
      base * fact(base - 1)
  }

  //局部函数
  def fact2(base: Int): Int = {
    def factHelper(n : Int) : Int = {
      fact2(n - 1)
    }
    if (base <= 0)
      1
    else
      base * factHelper(base)
  }

}
