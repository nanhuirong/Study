package com.huirong.java.concurrent.latch;

import java.util.concurrent.CountDownLatch;

/**
 * Created by huirong on 17-3-7.
 * 闭锁
 */
public class MyLatch {
    public long timeTasks(int nThreads, final Runnable task){
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++){
            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        startGate.await();
                        try {
                            task.run();
                        }finally {
                            endGate.countDown();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
        long start = System.nanoTime();
        startGate.countDown();
        try {
            endGate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        return end - start;
    }

}
