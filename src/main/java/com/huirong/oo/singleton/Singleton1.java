package com.huirong.oo.singleton;

/**
 * Created by huirong on 17-4-10.
 */
public class Singleton1 {
    /**
     * 饿汉模式，避免多线程的同步问题，
     * 缺点：不同的类加载器可能会加载同一个类导致一个类被加载多次
     * 解决方案：自行指定类加载器
     */
    private static Singleton1 instance = new Singleton1();
    private Singleton1(){

    }
    public static Singleton1 getInstance(){
        return instance;
    }
}
