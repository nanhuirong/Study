package com.huirong.java.concurrent.test;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Semaphore;

/**
 * Created by huirong on 17-3-12.
 */
@ThreadSafe
public class SemaphoreBoundedBuffer<E> {
    private final Semaphore avaliableItems, avaliableSpaces;
    @GuardedBy("this") private final E[] items;
    @GuardedBy("this") private int putPosition= 0, takePosition = 0;

    public SemaphoreBoundedBuffer(int capacity) {
        if (capacity <= 0){
            throw new IllegalArgumentException();
        }
        avaliableItems = new Semaphore(0);
        avaliableSpaces = new Semaphore(capacity);
        items = (E[]) new Object[capacity];
    }

    public boolean isEmpty(){
        return avaliableItems.availablePermits() == 0;
    }

    public boolean isFull(){
        return avaliableSpaces.availablePermits() == 0;
    }

    public void put(E elem) throws InterruptedException {
        avaliableSpaces.acquire();
        doInsert(elem);
        avaliableItems.release();
    }

    public E take() throws InterruptedException {
        avaliableItems.acquire();
        E item = doExtract();
        avaliableSpaces.release();
        return item;
    }

    private synchronized void doInsert(E elem){
        int i = putPosition;
        items[i] = elem;
        putPosition = (++i == items.length) ? 0 : i;
    }

    private synchronized E doExtract(){
        int i = takePosition;
        E elem = items[i];
        items[i] = null;
        takePosition = (++i == items.length) ? 0 : i;
        return elem;
    }
}
