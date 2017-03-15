package com.huirong.java.concurrent.tool;

import javax.annotation.concurrent.GuardedBy;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by huirong on 17-3-13.
 */
public class ConditionBoundedBuffer<T> {
    protected final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final static int BUFFER_SIZE = 100;
    @GuardedBy("lock")private final T[] items = (T[]) new Object[BUFFER_SIZE];
    @GuardedBy("lock")private int tail, head, count;
    public void put(T value) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length){
                notFull.await();
            }
            items[tail] = value;
            if (++tail == items.length){
                tail = 0;
            }
            ++count;
            notEmpty.signal();
        }finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0){
                notEmpty.await();
            }
            T value = items[head];
            items[head] = null;
            if (++head == items.length){
                head = 0;
            }
            --count;
            notFull.signal();
            return value;
        }finally {
            lock.unlock();
        }
    }
}
