package com.huirong.java.concurrent.tool;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by huirong on 17-3-13.
 */
@ThreadSafe
public class SemaphoreOnLock {
    private final Lock lock = new ReentrantLock();
    private final Condition permitsAvailable = lock.newCondition();
    @GuardedBy("lock") private int permits;

    public SemaphoreOnLock(int permits) {
        lock.lock();
        try {
            this.permits = permits;
        }finally {
            lock.unlock();
        }
    }

    public void acquare() throws InterruptedException {
        lock.lock();
        try {
            while (permits <= 0){
                permitsAvailable.await();
            }
            --permits;
        }finally {
            lock.unlock();
        }
    }

    public void release(){
        lock.lock();
        try {
            ++permits;
            permitsAvailable.signal();
        }finally {
            lock.unlock();
        }
    }
}
