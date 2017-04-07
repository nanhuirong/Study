package com.huirong.java.net.address;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by huirong on 17-3-20.
 */
public class PooledWebLog {
    private final static int NUM_THREADS = 4;
    private static class LogEntry{
        String original;
        Future<String> future;
        public LogEntry(String original, Future<String> future) {
            this.original = original;
            this.future = future;
        }
    }
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        Queue<LogEntry> results = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(args[0])))){
            for (String entry = br.readLine(); entry != null; entry = br.readLine()){
                LookupTask task = new LookupTask(entry);
                Future<String> future = executor.submit(task);
                LogEntry result = new LogEntry(entry, future);
                results.add(result);
            }
        }catch (IOException e){
            //ingore
        }
        //开始打印结果, 每次结果未准备就绪就会阻塞
        for (LogEntry entry : results){
            try {
                System.out.println(entry.future.get());
            }catch (InterruptedException | ExecutionException ex){
                System.out.println(entry.original);
            }
        }
        executor.shutdown();
    }
}
