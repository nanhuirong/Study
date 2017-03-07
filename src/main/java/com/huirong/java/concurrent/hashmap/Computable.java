package com.huirong.java.concurrent.hashmap;

/**
 * Created by huirong on 17-3-7.
 */
public interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}
