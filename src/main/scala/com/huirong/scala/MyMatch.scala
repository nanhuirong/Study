package com.huirong.scala

/**
  * Created by huirong on 17-3-6.
  */

case class MyClass(para: Double)



object MyMatch {
  def main(args: Array[String]) = {
    val day = "Sunday"
    val frenchDayOfWeek = day match {
      case "Sunday" => "Dimanche"
      case "Monday" => "Lundi"
      case "Tuesday" => "Mard"
      case "Wednesday" => "Mercredi"
      case "Thursday" => "Jeudi"
      case "Friday" => "Vendred"
      case "Saturday" => "Smaedi"
      case _ => "Error"
    }
    println(frenchDayOfWeek)
    var alarm = MyClass(0.99)
    println(alarm.para)
    println(myMatch(alarm))
  }

  def myMatch(para: AnyRef) = {
    val msg = para match {
      case "shabi" => 0
      case MyClass(temp) => "shabi" + temp
      case _ => "ERROR"
    }
    msg
  }

}
