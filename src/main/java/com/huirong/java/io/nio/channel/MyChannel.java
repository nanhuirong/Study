package com.huirong.java.io.nio.channel;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by huirong on 17-3-2.
 * Channel读取数据到Buffers
 */
public class MyChannel {
    public static void main(String[] args)throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile("/home/huirong/ids.log", "rw");
        FileChannel channel = randomAccessFile.getChannel();
        //创建一个48byte的buffer
        ByteBuffer buffer = ByteBuffer.allocate(48);
        int bytesRead = channel.read(buffer);
        while (bytesRead != -1){
//            System.out.println(bytesRead);
            buffer.flip();
            while (buffer.hasRemaining()){
//                System.out.println((char) buffer.get());//读取1bit
            }
            buffer.clear();
            bytesRead = channel.read(buffer);
        }
        randomAccessFile.close();
    }
}
