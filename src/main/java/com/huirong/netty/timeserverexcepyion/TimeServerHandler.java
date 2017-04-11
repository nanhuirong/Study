package com.huirong.netty.timeserverexcepyion;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * Created by huirong on 17-4-10.
 */
public class TimeServerHandler extends ChannelHandlerAdapter {
    //读到一条消息后统计一次，然后发送应答消息给客户端
    private int counter;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        /**
         * ByteBuf 类似于ByteBuffer对象
         */
        ByteBuf buf = (ByteBuf)msg;
        byte[] request = new byte[buf.readableBytes()];
        buf.readBytes(request);
        String body = new String(request, "UTF-8")
                .substring(0, request.length - System.getProperty("line.separator").length());
        System.out.println("Time Server receive order :" + body +
                " ; the counter is " + ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                new Date(System.currentTimeMillis()).toString() : " BAD ORDER";
        currentTime += System.getProperty("line.separator");
        ByteBuf respone = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(respone);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx)
//            throws Exception {
//        ctx.flush();
//    }
}
