package com.huirong.jiangjuan.code;

import com.huirong.jiangjuan.code.download.Consumer;
import com.huirong.jiangjuan.code.download.Storage;
import com.huirong.jiangjuan.code.parse.Parse;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Huirong on 17/1/7.
 */
public class Main {
    public static void main(String[] args)throws Exception {
        Storage storage = new Storage();
        CountDownLatch start = new CountDownLatch(1);
        Consumer[] consumers = new Consumer[10];
        for (Consumer consumer: consumers){
            consumer = new Consumer(storage);
            consumer.start();
        }
        storage.start.countDown();
        System.out.println("程序开始");
        storage.latch.await();
        Parse.parseFiles();
        System.exit(0);
    }
}
