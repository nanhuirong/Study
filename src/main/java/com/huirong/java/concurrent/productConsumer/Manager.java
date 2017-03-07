package com.huirong.java.concurrent.productConsumer;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by huirong on 17-3-7.
 */
public class Manager {
    private static final int BOUND = 10;
    private static final int NUM_CONSUMER = Runtime.getRuntime().availableProcessors() / 2;

    public static void startIndexing(File[] roots){
        System.out.println(NUM_CONSUMER);
        BlockingQueue<File> queue = new LinkedBlockingQueue<>(BOUND);
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        };

        for (File root: roots){
            new Thread(new FileCrawler(queue, filter, root)).start();
        }

        for (int i = 0; i < NUM_CONSUMER; i++){
            new Thread(new Indexer(queue)).start();
        }
    }

    public static void main(String[] args) {
        File[] files = new File[1];
        files[0] = new File("/");
        long start = System.currentTimeMillis();
        Manager.startIndexing(files);
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
    }
}
