package com.huirong.java.concurrent.hashmap;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by huirong on 17-3-7.
 */
public class Memoizer4<A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
    private final Computable<A, V> computable;

    public Memoizer4(Computable<A, V> computable) {
        this.computable = computable;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        while (true){
            Future<V> result = cache.get(arg);
            if (result == null){
                Callable<V> call = new Callable<V>() {
                    @Override
                    public V call() throws Exception {
                        return computable.compute(arg);
                    }
                };
                FutureTask<V> task = new FutureTask<V>(call);
                result = cache.putIfAbsent(arg, task);
                if (result == null){
                    result = task;
                    task.run();
                }
            }
            try {
                return result.get();
            }catch (CancellationException e){
                cache.remove(arg, result);
            } catch (ExecutionException e){
                e.printStackTrace();
            }
            return null;
        }
    }

}
