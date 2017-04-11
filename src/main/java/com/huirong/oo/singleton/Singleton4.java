package com.huirong.oo.singleton;

/**
 * Created by huirong on 17-4-10.
 */
public enum Singleton4 {
    /**
     * 枚举类实现，避免线程同步，防止反序列化重新创建对象
     */
    INSTANCE;
    public void whateverMethod(){

    }
}
