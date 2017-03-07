package com.huirong.java.io.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by huirong on 17-3-5.
 * 异步IO  将来式
 *         回调式
 */
public class MyAIO {
    public static void main(String[] args) {
//        MyAIO.future();
        MyAIO.callBack();
    }

    //将来式
    public static void future(){
        try {
            Path path = Paths.get("/home/huirong/ids.log");
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(path);
            ByteBuffer buffer = ByteBuffer.allocate(61_091_162);
            Future<Integer> result = channel.read(buffer, 0);
            while (!result.isDone()){
                //做别的事情
                System.out.println("--------------------");
            }
            Integer bytesRead = result.get();
            System.out.println(bytesRead);
        }catch (IOException | InterruptedException | ExecutionException e){

        }

    }

    //回调式
    public static void callBack(){
        try {
            Path path = Paths.get("/home/huirong/ids.log");
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(path);
            ByteBuffer buffer = ByteBuffer.allocate(61_091_162);
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                //回调
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    System.out.println("回调");
                    System.out.println(result);
                    System.out.println("---------");
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    System.out.println("---------");
                }
            });

            Thread.sleep(100000);

        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

}
