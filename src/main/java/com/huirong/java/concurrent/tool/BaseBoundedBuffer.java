package com.huirong.java.concurrent.tool;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Created by huirong on 17-3-13.
 */
@ThreadSafe
public abstract class BaseBoundedBuffer<T> {
    @GuardedBy("this") private final T[] buffer;
    @GuardedBy("this") private int tail;
    @GuardedBy("this") private int head;
    @GuardedBy("this") private int count;

    protected BaseBoundedBuffer(int capacity){
        this.buffer = (T[])new Object[capacity];
    }

    protected synchronized final void doPut(T value){
        buffer[tail] = value;
        if (++tail == buffer.length)
            tail = 0;
        ++count;
    }

    protected synchronized final T doTake(){
        T value = buffer[head];
        buffer[head] = null;
        if (++head == buffer.length)
            head = 0;
        --count;
        return value;
    }

    public synchronized final boolean isFull(){
        return count == this.buffer.length;
    }

    public synchronized final boolean isEmpty(){
        return count == 0;
    }
}
