package com.huirong.oo.ch01;

/**
 * Created by huirong on 17-4-7.
 */
public class MuteQuack implements QuackBehavior {
    @Override
    public void quack() {
        System.out.println("silience");
    }
}
