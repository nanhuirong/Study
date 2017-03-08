package com.huirong.java.concurrent.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * Created by huirong on 17-3-8.
 * 通过信号量来控制任务提交的速率
 */
public class BoundedExecutor {
    private final Executor executor;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor executor, Semaphore semaphore) {
        this.executor = executor;
        this.semaphore = semaphore;
    }

    public void submitTasks(final Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        command.run();
                    }finally {
                        semaphore.release();
                    }
                }
            });
        }catch (RejectedExecutionException e){
            semaphore.release();
        }
    }
}
