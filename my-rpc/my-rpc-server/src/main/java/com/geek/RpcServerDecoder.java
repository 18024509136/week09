package com.geek;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 将ByteBuf转为RpcRequest对象
 *
 * @author huangxiaodi
 * @since 2021-05-25 17:28
 */
public class RpcServerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);
        RpcRequest rpcRequest = SerializationUtil.deserializer(data, RpcRequest.class);

        if (rpcRequest == null) {
            ctx.close();
        }
        out.add(rpcRequest);
    }
}
