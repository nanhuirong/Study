package com.huirong.java.concurrent.tool;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Created by huirong on 17-3-13.
 * 通过轮询和等待实现简单的阻塞队列
 */
@ThreadSafe
public class SleepyBoundedBuffer <T> extends BaseBoundedBuffer<T>{
    int sleep = 60;
    public SleepyBoundedBuffer(){
        this(100);
    }

    public SleepyBoundedBuffer(int size){
        super(size);
    }

    public void put(T value) throws InterruptedException {
        while (true){
            synchronized (this){
                if (!isFull()) {
                    doPut(value);
                    return;
                }
            }
            Thread.sleep(sleep);
        }
    }

    public T take() throws InterruptedException {
        while (true){
            synchronized (this){
                if (!isEmpty()){
                    return doTake();
                }
            }
            Thread.sleep(sleep);
        }
    }

}
