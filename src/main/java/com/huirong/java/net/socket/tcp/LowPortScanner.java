package com.huirong.java.net.socket.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by huirong on 17-3-21.
 * 查看指定主机前1024个端口那个安装了TCP服务器
 */
public class LowPortScanner {
    public static void main(String[] args) {
        String host = "localhost";
        for (int port = 0; port < 1024; port++){
            try {
                Socket socket = new Socket(host, port);
                System.out.println(port);
            }catch (UnknownHostException ex){

            }catch (IOException ex){
                //此端口不是一个tcp服务
            }
        }
    }
}
