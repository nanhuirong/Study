package com.huirong.java.concurrent.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huirong on 17-3-7.
 */
public class TaskExecutorWithShutdown {
    private final ExecutorService service = Executors.newCachedThreadPool();
    public void start(){
        try {
            ServerSocket socket = new ServerSocket(80);
            while (!service.isShutdown()){
                final Socket connection = socket.accept();
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        handleRequest(connection);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        //平滑结束线程池,
        service.shutdown();
    }

    public static void handleRequest(Socket connection){
        //
    }
}
