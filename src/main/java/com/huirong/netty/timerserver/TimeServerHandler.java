package com.huirong.netty.timerserver;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * Created by huirong on 17-4-10.
 */
public class TimeServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        /**
         * ByteBuf 类似于ByteBuffer对象
         */
        ByteBuf buf = (ByteBuf)msg;
        byte[] request = new byte[buf.readableBytes()];
        buf.readBytes(request);
        String body = new String(request, "UTF-8");
        System.out.println("Time Server receive order :" + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf respone = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(respone);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
        ctx.flush();
    }
}
