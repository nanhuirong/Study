package com.huirong.java.concurrent.threadpool;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Created by huirong on 17-3-8.
 * 定制
 */
public class TimingThreadPool extends ThreadPoolExecutor {
    public TimingThreadPool(){
        super(1, 1, 0L, TimeUnit.SECONDS, null);
    }
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final Logger logger = Logger.getLogger("TimingThreadPool");
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();


    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        logger.fine(String.format("Thread %s : start %s", t, r));
        startTime.set(System.nanoTime());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        try {
            long endTime = System.nanoTime();
            long taskTime = endTime - startTime.get();
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            logger.fine(String.format("thread %s: end %s , time=%dns", t, r, taskTime));
        }finally {
            super.afterExecute(r, t);
        }

    }

    @Override
    protected void terminated() {
        try {
            logger.info(String.format("Terminated: avg time=%dns", totalTime.get() / numTasks.get()));
        }finally {
            super.terminated();
        }

    }
}
