package com.huirong.java.concurrent.productConsumer;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

/**
 * Created by huirong on 17-3-7.
 */
public class FileCrawler implements Runnable {
    private final BlockingQueue<File> fileQueue;
    private final FileFilter fileFilter;
    private final File root;

    public FileCrawler(BlockingQueue<File> fileQueue, FileFilter fileFilter, File root) {
        this.fileQueue = fileQueue;
        this.root = root;
        this.fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || fileFilter.accept(file);
            }
        };
    }

    @Override
    public void run() {
        crawl(root);
    }

    private boolean alreadyIndexed(File file){
        return false;
    }

    private void crawl(File root){
        File[] entries = root.listFiles();
        if (entries != null){
            for (File entry : entries){
                if (entry.isDirectory()){
                    crawl(entry);
                }else if (!alreadyIndexed(entry)){
                    try {
                        fileQueue.put(entry);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
