package com.huirong.java.concurrent.semaphore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Created by huirong on 17-3-7.
 * 利用信号量作为容器边界
 */
public class BoundHashSet<T> {
    private final Set<T> set;
    private final Semaphore semaphore;

    public BoundHashSet(int bound) {
        this.set = Collections.synchronizedSet(new HashSet<T>());
        semaphore = new Semaphore(bound);
    }

    public boolean add(T obj)throws InterruptedException{
        semaphore.acquire();
        boolean wasAdded = false;
        try {
            wasAdded = set.add(obj);
            return wasAdded;
        }finally {
            if (!wasAdded){
                semaphore.release();
            }
        }
    }

    public boolean remove(Object obj){
        boolean wasRemoved = set.remove(obj);
        if (wasRemoved){
            semaphore.release();
        }
        return wasRemoved;
    }
}
