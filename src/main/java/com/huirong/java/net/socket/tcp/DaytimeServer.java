package com.huirong.java.net.socket.tcp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Created by huirong on 17-3-21.
 */
public class DaytimeServer {
    public static final int PORT = 10000;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)){
            while (true){
                try (Socket socket = server.accept()){
                    Writer writer = new OutputStreamWriter(socket.getOutputStream());
                    Date date = new Date();
                    writer.write(date.toString() + "\r\n");
                    writer.flush();
                    socket.close();
                }
            }
        }catch (IOException ex){
            System.err.println(ex);
        }
    }
}
