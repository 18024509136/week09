package com.geek;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 将RpcRequest对象转为Byte发送到服务端
 *
 * @author huangxiaodi
 * @since 2021-05-25 17:22
 */
public class RpcClientEncoder extends MessageToByteEncoder<RpcRequest> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {
        byte[] bytes = SerializationUtil.serializer(msg);
        out.writeBytes(bytes);
        ctx.flush();
    }
}
