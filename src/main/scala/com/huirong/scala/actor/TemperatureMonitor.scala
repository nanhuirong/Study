package com.huirong.scala.actor

import scala.actors._
/**
  * Created by huirong on 17-3-6.
  */
case object Heartbeat
case class TempperatureAlarm(threshold: Double)
class TemperatureMonitor extends Actor{
  var tripped : Boolean = false
  var tripTemp : Double =  0.0

  override def act() = {
    while (true){
      receive{
        case Heartbeat => 0
        case TempperatureAlarm(threshold) =>
          tripped = true
          tripTemp = threshold
        case _ => println("no match")
      }
    }
  }
}
