package com.huirong.java.concurrent.cancle;

import javax.annotation.concurrent.GuardedBy;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

/**
 * Created by huirong on 17-3-8.
 */
public class LogService {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;
    @GuardedBy("this") private boolean isShutDown;
    @GuardedBy("this") private int reservations;

    public LogService(BlockingQueue<String> queue, LoggerThread loggerThread, PrintWriter writer) {
        this.queue = queue;
        this.loggerThread = loggerThread;
        this.writer = writer;
    }
    public void start(){
        loggerThread.start();
    }
    public void stop(){
        synchronized (this){
            isShutDown = true;
        }
        loggerThread.interrupt();
    }

    public void log(String msg)throws InterruptedException{
        synchronized (this){
            if (isShutDown){
                return;
            }
            ++reservations;
        }
        queue.put(msg);
    }


    private class LoggerThread extends Thread{
        @Override
        public void run() {
            try {
                while (true){
                    try {
                        synchronized (LogService.this){
                            if (isShutDown && reservations == 0){
                                break;
                            }
                        }
                        String msg = queue.take();
                        synchronized (LogService.this){
                            --reservations;
                        }
                        writer.println(msg);
                    }catch (InterruptedException e){
                        //retry
                    }
                }
            }finally {
                writer.close();
            }
        }
    }
}
