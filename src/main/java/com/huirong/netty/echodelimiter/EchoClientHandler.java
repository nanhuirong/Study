package com.huirong.netty.echodelimiter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

/**
 * Created by huirong on 17-4-11.
 */
public class EchoClientHandler extends ChannelHandlerAdapter {
    public static final Logger LOGGER = Logger.getLogger(EchoClientHandler.class);
    private int counter;
    static final String ECHO_REQ = "Hi, nanhuirong.github.io welcome to netty $_";
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getCause());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0 ; i < 100; i++){
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("This is " + ++counter + " times receive server: [" + msg + "]");
    }
}
