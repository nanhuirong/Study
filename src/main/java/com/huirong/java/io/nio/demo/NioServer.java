package com.huirong.java.io.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by huirong on 17-3-3.
 */
public class NioServer {
    public static void main(String[] args)throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().bind(new InetSocketAddress(9000), 10);
        channel.configureBlocking(false);
        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_ACCEPT);
        while (true){
            selector.select();
            Iterator iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();
                Handle(key, selector);
            }
        }
    }


    public static void Handle(SelectionKey key, Selector selector)throws IOException{
        ServerSocketChannel server = null;
        SocketChannel client = null;
        if (key.isAcceptable()){
            System.out.println("Acceptable");
            server = (ServerSocketChannel) key.channel();
            client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        }else if (key.isReadable()){
            client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(200);
            int count = client.read(buffer);
            if (count > 0){
                System.out.println("readable");
                System.out.println(new String(buffer.array()));
            }else if (count == -1){
                key.cancel();
                return;
            }
        }
    }
}
