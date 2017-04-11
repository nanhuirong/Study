package com.huirong.netty.codec.msgpack;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by huirong on 17-4-11.
 */
public class EchoServerHandler extends ChannelHandlerAdapter {
    public static final Logger LOGGER = Logger.getLogger(EchoServerHandler.class.getName());
    private int counter;
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getStackTrace());
        LOGGER.error(cause.getMessage());
        ctx.close();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        List<Object> userInfos = (List<Object>) msg;
        LOGGER.info(userInfos);
        ctx.writeAndFlush(userInfos);

//        LOGGER.info(msg);
//        ctx.writeAndFlush(msg);
    }
}
