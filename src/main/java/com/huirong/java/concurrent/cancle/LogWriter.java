//package com.huirong.java.concurrent.cancle;
//
//import java.io.PrintWriter;
//import java.util.concurrent.BlockingQueue;
//
///**
// * Created by huirong on 17-3-8.
// * 设计一个多生产者-单消费者的打印日志服务
// */
//public class LogWriter {
//    private final BlockingQueue<String> queue;
//    private final LoggerThread logger;
//    private final static int CAPACITY = 1000;
//
//    public LogWriter(BlockingQueue<String> queue, LoggerThread logger) {
//        this.queue = queue;
//        this.logger = logger;
//    }
//    public void start(){
//        logger.start();
//    }
//
//    public void log(String message){
//        try {
//            queue.put(message);
//        } catch (InterruptedException ingore) {
//
//        }
//    }
//
//    private class LoggerThread extends Thread{
//        private final PrintWriter writer;
//
//        public LoggerThread(PrintWriter writer) {
//            this.writer = writer;
//        }
//
//        @Override
//        public void run() {
//            try {
//                while (true){
//                    writer.println(queue.take());
//                }
//            }catch (InterruptedException ingore){
//
//            }finally {
//                writer.close();
//            }
//        }
//    }
//}
