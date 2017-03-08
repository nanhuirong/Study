package com.huirong.java.concurrent.cancle;

import java.util.*;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by huirong on 17-3-8.
 */
public class TrackingExecutor  extends AbstractExecutorService{
    private final ExecutorService executor;
    private final Set<Runnable> tasksCanceledAtShutWodn = Collections.synchronizedSet(new HashSet<>());

    public TrackingExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void shutdown(){
        executor.shutdown();
    }


    public boolean isTerminated(){
        return executor.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    public List<Runnable> getCanceledTasks(){
        if (!executor.isTerminated()){
            throw new IllegalStateException();
        }
        return new ArrayList<Runnable>(tasksCanceledAtShutWodn);
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    command.run();
                }finally {
                    if (isShutdown() && Thread.currentThread().isInterrupted()){
                        tasksCanceledAtShutWodn.add(command);
                    }
                }
            }
        });
    }
}
