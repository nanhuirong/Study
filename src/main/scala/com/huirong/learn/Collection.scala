package com.huirong.learn


import util.{Failure, Random, Success}
import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

/**
  * Created by huirong on 17-4-17.
  */
object Collection {

  def main(args: Array[String]): Unit = {
//    loopAndFail(10, 3)
//    val t1 = util.Try(loopAndFail(2, 3))
//
//    util.Try(loopAndFail(2, 1)) match {
//      case Success(x) => println(x)
//      case Failure(error) => println(error)
//    }
//
//    val f = concurrent.Future{
//      println("shabi")
//    }

    val temps = Future.sequence(Seq(
      Future(cityTemp("Fresno")), Future(cityTemp("Tempe"))
    ))
    temps.onSuccess{
      case Seq(x, y) if(x > y) => println(x)
      case Seq(x, y) if(x <= y) => println(y)
    }
  }

  def nextOption = if (util.Random.nextInt() > 0) Some(1) else None

  def loopAndFail(end: Int, failAt: Int): Int = {
    for(i <- 1 to end){
      println(s"$i ")
      if(i == failAt)
        throw new Exception("too many iteration")
    }
    end
  }

  def nextFuture(i: Int)= Future {
    def rand(x: Int) = Random.nextInt(x)
    Thread.sleep(rand(5000))
    if (rand(3) > 0)
      (i + 1)
    else
      throw new Exception
  }


  def cityTemp(name: String): Double = {
    val url = "http://api.openweathermap.org/data/2.5/weather"
    val cityUrl = s"$url?q=$name"
    val json = Source.fromURL(cityUrl).mkString.trim
    val pattern = """.*"temp":([\d.]+).*""".r
    val pattern(temp) = json
    temp.toDouble
  }

  class User(val name: String){
    def greet: String = s"Hello from $name"

    override def toString: String = s"User($name)"
  }

  class A{
    def hi = "hi"

    override def toString: String = getClass.getName
  }

  trait B{ self: A =>
    override def toString: String = "B: " + hi
  }
  class C extends A with B

  class Singular[A](element: A) extends Traversable[A]{
    override def foreach[B](f: (A) => B) = f(element)
  }

  abstract class Car{
    val year: Int
    val automatic: Boolean = true
    def color: String
  }

  class RedMini(val year: Int) extends Car{
    override def color: String = "Red"
  }

  class Mini(val year: Int, val color: String) extends Car

  abstract class Listener{
    def trigger
  }
  class Listening{
    var listener: Listener = null
    def register(l: Listener): Unit ={
      listener = l
    }
    def sendNotification(): Unit ={
      listener.trigger
    }
  }

  class Multiplier(factor: Int) {
    def apply(input: Int): Int = input * factor
  }

  class RandomPoint{
    val x = {
      println("creating x")
      util.Random.nextInt()
    }
    lazy val y = {
      println("creating y")
      util.Random.nextInt()
    }
  }

  class TestSuit(suitName: String){
    def start(){}
  }
  trait RandomSeed{self: TestSuit =>
    def randomStart() ={
      util.Random.setSeed(System.currentTimeMillis())
      self.start()
    }

  }
  class IdSpec extends TestSuit("ID tests") with RandomSeed{
    def testId ={
      println(util.Random.nextInt() != 1)
    }

    override def start(){testId}
    println("starting...")
    randomStart()
  }

}
