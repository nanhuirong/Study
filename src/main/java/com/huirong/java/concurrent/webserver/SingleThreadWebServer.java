package com.huirong.java.concurrent.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by huirong on 17-3-7.
 * 串行处理Web请求
 */
public class SingleThreadWebServer {
    public static void main(String[] args) {
        try {
            ServerSocket socket = new ServerSocket(80);
            while (true){
                Socket connection = socket.accept();
                handleRequest(connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleRequest(Socket connection){
        //
    }
}
