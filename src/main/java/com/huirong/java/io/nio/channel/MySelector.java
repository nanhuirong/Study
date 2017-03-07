package com.huirong.java.io.nio.channel;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by huirong on 17-3-2.
 */
public class MySelector {
    public static void main(String[] args)throws Exception {
//      打开一个Selector
        Selector selector = Selector.open();
        //Channel 必须是非阻塞的
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
        while (true){
            int readyChannels = selector.select();
            if (readyChannels == 0) continue;
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()){
                    // a connection was accepted by a ServerSocketChannel
                }else if (selectionKey.isConnectable()){
                    //a connection was established with a remote server
                }else if (selectionKey.isReadable()){
                    // a channel is ready for reading
                }else if (selectionKey.isWritable()){
                    //a channel is ready for writing
                }
            }
            iterator.remove();
        }
    }
}
