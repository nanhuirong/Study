package com.huirong.java.net.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by huirong on 17-3-23.
 */
public class DaytimeUDPClient {
    private static final int PORT = 10000;
    private static final String HOSTNAME = "localhost";

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(0)){
            socket.setSoTimeout(10000);
            InetAddress  host = InetAddress.getByName(HOSTNAME);
            DatagramPacket request = new DatagramPacket(new byte[1], 1, host, PORT);
            DatagramPacket respone = new DatagramPacket(new byte[1024], 1024);
            socket.send(request);
            socket.receive(respone);
            String result = new String(respone.getData(), 0, respone.getLength(), "US-ASCII");
            System.out.println(result);
        }catch (IOException ex){
            //ignore
        }
    }

}
