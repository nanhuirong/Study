package com.huirong.java.concurrent.cancle;

import java.util.concurrent.*;

/**
 * Created by huirong on 17-3-8.
 */
public class TimeRun {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    public static void timeRun(Runnable run, long timeout, TimeUnit unit){
        Future<?> task = executor.submit(run);
        try {
            task.get(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            task.cancel(true);
        }
    }
}
