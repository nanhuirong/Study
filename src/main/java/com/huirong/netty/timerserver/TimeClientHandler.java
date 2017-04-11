package com.huirong.netty.timerserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

/**
 * Created by huirong on 17-4-10.
 */
public class TimeClientHandler extends ChannelHandlerAdapter {
    private static final Logger LOGGER =
            Logger.getLogger(TimeClientHandler.class.getName());
    private final ByteBuf firstMessage;

    public TimeClientHandler() {
        byte[] request = "QUERY TIME ORDER".getBytes();
        firstMessage = Unpooled.buffer(request.length);
        firstMessage.writeBytes(request);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] request = new byte[buf.readableBytes()];
        buf.readBytes(request);
        String body = new String(request, "UTF-8");
        System.out.println("Now is : " + body);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        LOGGER.warn("Unexpected exception from downstream : "
                + cause.getMessage());
        ctx.close();
    }
}
