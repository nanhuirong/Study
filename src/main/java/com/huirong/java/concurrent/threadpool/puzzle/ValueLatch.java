package com.huirong.java.concurrent.threadpool.puzzle;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.CountDownLatch;

/**
 * Created by huirong on 17-3-9.
 */
@ThreadSafe
public class ValueLatch<T> {
    @GuardedBy("this") private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);
    public boolean isSet(){
        return (done.getCount() == 0);
    }

    public synchronized void setValue(T newValue){
        if (!isSet()){
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        done.wait();
        synchronized (this){
            return value;
        }
    }
}
