package com.huirong.oo.singleton;

/**
 * Created by huirong on 17-4-10.
 */
public class Singleton2 {
    /**
     * 饿汉模式，与Singleton1的方法差不多
     */
    private static Singleton2 instance = null;
    static {
        instance = new Singleton2();
    }
    private Singleton2(){

    }
    public static Singleton2 getInstance(){
        return instance;
    }
}
