package com.huirong.java.concurrent.productConsumer;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * Created by huirong on 17-3-7.
 */
public class Indexer implements Runnable {
    private final BlockingQueue<File> queue;

    public Indexer(BlockingQueue<File> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true){
                indexFile(queue.take());
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public void indexFile(File file){
        //index the files
        System.out.println(file.getName());
    }
}
