package com.huirong.java.concurrent.cancle;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * Created by huirong on 17-3-8.
 * 通过中断来取消线程
 */
public class PrimeProducer implements Runnable{
    private final BlockingQueue<BigInteger> queue;

    public PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!Thread.currentThread().isInterrupted()){
                queue.put(p = p.nextProbablePrime());
            }
        }catch (InterruptedException e){

        }
    }

    public void cancel(){
        Thread.currentThread().interrupt();
    }
}
