package com.huirong.java.concurrent.tool;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Created by huirong on 17-3-13.
 * 基于条件队列实现缓冲区
 * 尽量避免使用条件队列, 基于LinkedBlockingQueue, Latch, Semaphore, FutureTask构建程序
 */
@ThreadSafe
public class BoundedBuffer<T> extends BaseBoundedBuffer<T> {
    public BoundedBuffer(int size){
        super(size);
    }

    public synchronized void put(T value) throws InterruptedException {
        while (isFull()){
            wait();
        }
        doPut(value);
        if (isEmpty())
            notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (isEmpty()){
            wait();
        }
        T value = doTake();
        notifyAll();
        return value;
    }
}
