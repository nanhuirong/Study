package com.huirong.java.concurrent.hashmap;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by huirong on 17-3-7.
 * 其实还是存在修改相同值的可能性
 */
public class Memoizer3<A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
    private final Computable<A, V> computable;

    public Memoizer3(Computable<A, V> computable) {
        this.computable = computable;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        Future<V> result = cache.get(arg);
        if (result == null){
            Callable<V> call = new Callable<V>() {
                @Override
                public V call() throws Exception {
                    return computable.compute(arg);
                }
            };
            FutureTask<V> task = new FutureTask<V>(call);
            result = task;
            cache.put(arg, task);
            task.run();
        }
        try {
            return result.get();
        }catch (ExecutionException e){
            e.printStackTrace();
        }
        return null;
    }
}
