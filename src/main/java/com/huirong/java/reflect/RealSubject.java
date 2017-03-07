package com.huirong.java.reflect;

/**
 * Created by huirong on 17-3-6.
 */
public class RealSubject implements Subject {
    @Override
    public String say(String name, int age) {
        return name + "-" + age;
    }
}
