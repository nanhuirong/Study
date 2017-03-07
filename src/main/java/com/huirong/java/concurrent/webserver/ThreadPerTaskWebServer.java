package com.huirong.java.concurrent.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by huirong on 17-3-7.
 */
public class ThreadPerTaskWebServer {

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
                new Thread(task).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void handleRequest(Socket connection){
        //
    }
}
