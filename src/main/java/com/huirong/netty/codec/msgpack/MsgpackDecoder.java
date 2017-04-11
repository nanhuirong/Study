package com.huirong.netty.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * Created by huirong on 17-4-11.
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf,
                          List<Object> list) throws Exception {
        /**
         * 从ByteBuf数组中获取byte数组，并反序列化为Object对象，
         */
        final byte[] array;
        final int length = byteBuf.readableBytes();
        array = new byte[length];
        byteBuf.getBytes(byteBuf.readableBytes(), array, 0, length);
        MessagePack msgpack = new MessagePack();
        list.add(msgpack.read(array));
    }
}
