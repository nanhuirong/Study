package com.huirong.netty.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * Created by huirong on 17-4-11.
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          Object object,
                          ByteBuf byteBuf) throws Exception {
        /**
         * 将Object类型的对象编码为byte数组，写入ByteBuf中
         */
        MessagePack msgpack = new MessagePack();
        byte[] raw = msgpack.write(object);
        byteBuf.writeBytes(raw);
    }
}
