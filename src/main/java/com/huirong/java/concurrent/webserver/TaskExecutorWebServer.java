package com.huirong.java.concurrent.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by huirong on 17-3-7.
 * 内部维护一个线程池
 */
public class TaskExecutorWebServer {
    private static final int NUM_THREADS = 100;
    private static final Executor executor = Executors.newFixedThreadPool(NUM_THREADS);
    public static void main(String[] args) {
        try {
            ServerSocket socket = new ServerSocket(80);
            while (true){
                final Socket connection = socket.accept();
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        handleRequest(connection);
                    }
                };
                executor.execute(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleRequest(Socket connection){
        //
    }
}
