package com.huirong.oo.singleton;

/**
 * Created by huirong on 17-4-10.
 */
public class Singleton5 {
    /**
     * 双重校验锁，Singleton类中懒汉方法的改进
     * JDK1.5之前的版本不适合这种方法
     */
    private volatile static Singleton5 instance;
    private Singleton5(){}
    public static Singleton5 getInstance(){
        if (instance == null){
            synchronized (Singleton5.class){
                if (instance == null){
                    instance = new Singleton5();
                }
            }
        }
        return instance;
    }
}
