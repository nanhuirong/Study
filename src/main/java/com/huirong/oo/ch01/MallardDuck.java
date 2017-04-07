package com.huirong.oo.ch01;

/**
 * Created by huirong on 17-4-7.
 */
public class MallardDuck extends Duck {
    public MallardDuck() {
        quackBehavior = new Quack();
        flyBehavior = new FlyWithWings();
    }
}
