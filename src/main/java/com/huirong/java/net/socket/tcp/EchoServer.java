package com.huirong.java.net.socket.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by huirong on 17-3-21.
 */
public class EchoServer {
    public static final int PORT = 10000;

    public static void main(String[] args) {
        ServerSocketChannel serverChannel;
        Selector selector;
        try {
            serverChannel = ServerSocketChannel.open();
            ServerSocket socket = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(PORT);
            socket.bind(address);
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (IOException ex){
            return;
        }

        while (true){
            try {
                selector.select();
            }catch (IOException ex){
                break;
            }
        }

        Set<SelectionKey> readKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = readKeys.iterator();
        while (iterator.hasNext()){
            SelectionKey key = iterator.next();
            iterator.remove();
            try {
                if (key.isAcceptable()){
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    System.out.println("accepted connection from " + client);
                    client.configureBlocking(false);
                    SelectionKey clientKey = client.register(
                            selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                    ByteBuffer buffer = ByteBuffer.allocate(100);
                    clientKey.attach(buffer);
                }
                if (key.isReadable()){
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    client.read(buffer);
                }
                if (key.isWritable()){
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    buffer.flip();
                    client.write(buffer);
                    buffer.compact();
                }
            }catch (IOException ex){
                key.cancel();
                try {
                    key.channel().close();
                }catch (IOException ingore){

                }
            }
        }
    }
}
