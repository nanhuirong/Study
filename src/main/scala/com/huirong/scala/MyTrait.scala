package com.huirong.scala

/**
  * Created by huirong on 17-3-6.
  */
class Pet(name: String)

trait Chipped {
  var chipName: String
  def getName = chipName
}

class Cat(name: String)  extends Pet(name: String) with Chipped{
  override var chipName: String = name
}


class MyTrait {

}
