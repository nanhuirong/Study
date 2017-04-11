package com.huirong.netty.timerserverperfect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by huirong on 17-4-10.
 */
public class TimeClient {
    public void connect(int port, String host)throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel socketChannel)
                                throws Exception {
                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            //发起异步链接操作
            ChannelFuture future = b.connect(host, port).sync();
            //等待客户端链路关闭
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args)throws Exception {
        int port = 8080;
        new TimeClient().connect(port, "127.0.0.1");
//        while (true){
//            for (int i = 0; i < 1000; i++){
//                new TimeClient().connect(port, "127.0.0.1");
//
//            }
//        }
    }
}
