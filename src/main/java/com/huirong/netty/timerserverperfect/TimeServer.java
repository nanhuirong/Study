package com.huirong.netty.timerserverperfect;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by huirong on 17-4-10.
 */
public class TimeServer {
    public void bind(int port)throws Exception{
        //配置服务端的NIO线程组
        /**
         * EventLoopGroup 是一个线程组，包含一组NIO线程（Reactor线程组），
         */
        //处理客户端请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //进行SocketChannel读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**
             * ServerBootstrap NIO服务端启动辅助类
             */
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    //设置Channel属性，功能对应JDK NIO的ServerSocketChannel类
                    .channel(NioServerSocketChannel.class)
                    //设置TCP参数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //绑定IO事件的处理类，类似Reactor模式中的Handler类
                    .childHandler(new ChildChannelHandler());
            //绑定端口，同步等待成功
            /**
             * ChannelFuture 功能类似与java.util.concurrent.Future，用于异步操作的通知回调
             */
            ChannelFuture future = b.bind(port).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        }finally {
            //释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            /**
             * LineBasedFrameDecoder：依次遍历ByteBuf中的可读字符，判断是否有\n或者\r\n，
             * 本质是以换行作为结束标志，同时配置单行的最大长度
             */
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            /**
             * StringDecoder 将接受到的对象转换为字符串
             */
            socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args)throws Exception {
        int port = 8080;
        new TimeServer().bind(port);
    }
}
