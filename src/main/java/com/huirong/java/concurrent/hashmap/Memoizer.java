package com.huirong.java.concurrent.hashmap;

import javax.annotation.concurrent.GuardedBy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huirong on 17-3-7.
 * 利用HashMap作为缓存
 * 同一时刻只有一个线程访问
 */
public class Memoizer<A, V> implements Computable<A, V> {
    @GuardedBy("this")
    private final Map<A, V> cache = new HashMap<A, V>();
    private final Computable<A, V> computable;

    public Memoizer(Computable<A, V> computable) {
        this.computable = computable;
    }

    @Override
    public synchronized V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null){
            result = computable.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}
