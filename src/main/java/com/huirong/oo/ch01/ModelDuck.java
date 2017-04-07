package com.huirong.oo.ch01;

/**
 * Created by huirong on 17-4-7.
 */
public class ModelDuck extends Duck{
    public ModelDuck() {
        flyBehavior = new FlyNoWay();
        quackBehavior = new Quack();
    }

    public void display(){
        System.out.println("I am a model duck");
    }
}
