package com.huirong.oo.singleton;

/**
 * Created by huirong on 17-4-10.
 */
public class Singleton3 {
    /**
     * 内部静态类
     */
    private static class SingletonHandle{
        private static final Singleton3 INSTANCE = new Singleton3();
    }
    private Singleton3(){}
    public static final Singleton3 getInstance(){
        return SingletonHandle.INSTANCE;
    }
}
