package com.huirong.java.concurrent.cancle.pill;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

/**
 * Created by huirong on 17-3-8.
 * 多生产者必须一个生产者放入一个毒丸对象
 */
public class IndexService {
    private static final int CAPACITY = 1000;
    //毒丸对象
    private static final File POISON = new File("");
    private final IndexedThread consumer = new IndexedThread();
    private final ClawerThread producer = new ClawerThread();
    private final BlockingQueue<File> queue;
    private final FileFilter fileFilter;
    private final File root;

    public IndexService(BlockingQueue<File> queue, FileFilter fileFilter, File root) {
        this.queue = queue;
        this.fileFilter = fileFilter;
        this.root = root;
    }
    private boolean alreadyIndexed(File file){
        return false;
    }

    class ClawerThread extends Thread{
        @Override
        public void run() {
            try {
                claw(root);
            }catch (InterruptedException ingore){

            }finally {
                while (true){
                    try {
                        queue.put(POISON);
                        break;
                    }catch (InterruptedException ingore){
                        //retry
                    }
                }
            }
        }

        private void claw(File root) throws InterruptedException {
            File[] files = root.listFiles(fileFilter);
            if (files != null){
                for (File entry : files){
                    if (entry.isDirectory()){
                        claw(entry);
                    }else if (!alreadyIndexed(entry)){
                        queue.put(entry);
                    }
                }
            }
        }
    }

    class IndexedThread extends Thread{
        @Override
        public void run() {
            try {
                while (true){
                    File file = queue.take();
                    if (file == POISON){
                        break;
                    }else {
                        indexFile(file);
                    }
                }
            }catch (InterruptedException ingore){

            }
        }

        public void indexFile(File file){

        }
    }

    public void start(){
        producer.start();
        consumer.start();
    }

    public void stop(){
        producer.interrupt();
    }

    public void awaitTermination() throws InterruptedException{
        consumer.join();
    }

}
