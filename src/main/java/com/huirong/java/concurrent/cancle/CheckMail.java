package com.huirong.java.concurrent.cancle;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by huirong on 17-3-8.
 */
public class CheckMail {
    public boolean checkMail(Set<String> hosts, long timeout, TimeUnit unit) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        final AtomicBoolean hasNewMail = new AtomicBoolean(false);
        try {
            for (final String host : hosts){
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (checkMail(host)){
                            hasNewMail.set(true);
                        }
                    }
                });
            }
        }finally {
            executor.shutdown();
            executor.awaitTermination(timeout, unit);
        }
        return hasNewMail.get();
    }

    private boolean checkMail(String host){
        return false;
    }
}
