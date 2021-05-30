package com.geek;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 将RpcResponse对象转为字节数组并发送给客户端
 *
 * @author huangxiaodi
 * @since 2021-05-25 17:22
 */
public class RpcServerEncoder extends MessageToByteEncoder<RpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse msg, ByteBuf out) {
        byte[] bytes = SerializationUtil.serializer(msg);
        out.writeBytes(bytes);
        ctx.flush();
    }
}
