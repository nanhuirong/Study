package com.huirong.java.net.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by huirong on 17-3-22.
 */
public class ChargenClient {
    public static String HOST = "";
    public static int PORT = 10000;

    public static void main(String[] args) {
        byte[] rotation = new byte[95 * 2];
        for (byte i = ' '; i <= '~'; i++){
            rotation[i - ' '] = i;
            rotation[i + 95 - ' '] = i;
        }
        ServerSocketChannel serverChannel;
        Selector selector;
        try {
            serverChannel = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(PORT);
            serverChannel.bind(address);
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (IOException ex){
            return;
        }

        while (true){
            try {
                selector.select();
//                System.out.println("-----------");
            } catch (IOException e) {
                System.out.println("break");
                break;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    //服务端
                    if (key.isAcceptable()){
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("accepted connection from " + client);
                        client.configureBlocking(false);
                        SelectionKey key1 = client.register(selector, SelectionKey.OP_WRITE);
                        ByteBuffer buffer = ByteBuffer.allocate(74);
                        buffer.put(rotation, 0, 72);
                        buffer.put((byte) '\r');
                        buffer.put((byte) '\n');
                        //读模式调整为写模式
                        buffer.flip();
                        //将buffer链接给这个键
                        key1.attach(buffer);
                    }else if (key.isWritable()){
                        //客户端
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        if (!buffer.hasRemaining()){
                            //用下一行重新填充缓冲区
                            buffer.rewind();
                            //得到上一次的首字符
                            int first = buffer.get();
                            //准备改变缓冲区的数据
                            buffer.rewind();
                            //寻找rotation中的新的首字符位置
                            int position = first - ' ' + 1;
                            //将数据复制到缓存
                            buffer.put(rotation, position, 72);
                            buffer.put((byte)'\r');
                            buffer.put((byte)'\n');
                            buffer.flip();
                        }
                        client.write(buffer);
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
}
