package com.huirong.netty.codec.msgpack;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

/**
 * Created by huirong on 17-4-11.
 */
public class EchoClientHandler extends ChannelHandlerAdapter {
    private final int sendNumber;
    private static final Logger LOGGER = Logger.getLogger(EchoClientHandler.class);

    public EchoClientHandler(int sendNumber) {
        this.sendNumber = sendNumber;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getStackTrace());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserInfo[] infos = userInfo();
        for (UserInfo info : infos){
//            LOGGER.info(info);
            ctx.writeAndFlush(info);
        }
//        ctx.flush();
    }
    private UserInfo[] userInfo(){
        UserInfo[] infos = new UserInfo[sendNumber];
        UserInfo info = null;
        for (int i = 0; i < sendNumber; i++){
            info = new UserInfo(i, "ABCD-" + i);
            infos[i] = info;
        }
        return infos;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("Client receive the msgpack message : " + msg);
//        ctx.writeAndFlush(msg);
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
//    }
}
