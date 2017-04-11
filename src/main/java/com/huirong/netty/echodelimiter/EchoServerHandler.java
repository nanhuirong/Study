package com.huirong.netty.echodelimiter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

/**
 * Created by huirong on 17-4-11.
 */
public class EchoServerHandler extends ChannelHandlerAdapter {
    public static final Logger LOGGER = Logger.getLogger(EchoServerHandler.class.getName());
    private int counter;
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getCause());
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("this is " + ++counter + " times receive client: [" + msg + "]");
        String message = (String) msg + "$_";
        ByteBuf echo = Unpooled.copiedBuffer(message.getBytes());
        ctx.writeAndFlush(echo);
    }
}
