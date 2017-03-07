package com.huirong.java.io.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by huirong on 17-3-3.
 */
public class NioClient {
    public static void main(String[] args) throws IOException{
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress("127.0.0.1", 9000));

        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_CONNECT);
        while (true){
            //轮询
            selector.select();
            //获取可读
            Iterator iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = (SelectionKey)iterator.next();
                iterator.remove();
                if (key.isConnectable()){
                    Handle(key, selector);
                }
            }
        }
    }

    public static void Handle(SelectionKey key, Selector selector)throws IOException{
        SocketChannel client = (SocketChannel) key.channel();
        if (client.isConnectionPending()){
            if (client.finishConnect()){
                ByteBuffer buffer = ByteBuffer.allocate(200);
                buffer = ByteBuffer.wrap(new String("hello server").getBytes());
                client.write(buffer);
                client.register(selector, SelectionKey.OP_READ);
            }
        }else if (key.isReadable()){

        }
    }
}
