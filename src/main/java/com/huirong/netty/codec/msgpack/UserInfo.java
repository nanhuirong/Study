package com.huirong.netty.codec.msgpack;

import org.msgpack.annotation.Message;

/**
 * Created by huirong on 17-4-11.
 */
//此处必须由
@Message
public class UserInfo {
    private int age;
    private String name;
    //必须由空构造函数
    public UserInfo() {
    }

    public UserInfo(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "[" +
                "age=" + age +
                ", name=" + name +
                "]";
    }
}
