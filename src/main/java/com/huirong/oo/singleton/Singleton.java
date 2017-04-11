package com.huirong.oo.singleton;

/**
 * Created by huirong on 17-4-10.
 */
public class Singleton {
    private static Singleton instance;
    private Singleton(){

    }

    /**
     * 懒汉模式1
     * 该方法在多线程模式下无法正常工作
     * @return
     */
    public static Singleton getInstanceV1(){
        if (instance == null){
            instance = new Singleton();
        }
        return instance;
    }

    /**
     * 懒汉模式2
     * 在多线程模式下能正常工作，但是效率比较低
     * @return
     */
    public static synchronized Singleton getInstanceV2(){
        if (instance == null){
            instance = new Singleton();
        }
        return instance;
    }
}
