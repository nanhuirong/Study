package com.huirong.java.concurrent.cancle;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by huirong on 17-3-8.
 */
public class PrimGenerator implements Runnable {
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private final List<BigInteger> prims = new ArrayList<>();
    //取消标记位
    private volatile boolean cancelled;

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled){
            p = p.nextProbablePrime();
            synchronized (this){
                prims.add(p);
            }
        }
    }

    public void cancel(){
        cancelled = true;
    }
    public synchronized List<BigInteger> get(){
        return new ArrayList<>(prims);
    }
    static List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        PrimGenerator generator = new PrimGenerator();
        executor.execute(generator);
        try {
            SECONDS.sleep(1);
        }finally {
            generator.cancel();
        }
        return generator.get();
    }
}
