package com.geek;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 将ByteBuf转为RpcResposne对象
 *
 * @author huangxiaodi
 * @since 2021-05-25 17:28
 */
public class RpcClientDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);
        RpcResponse rpcResponse = SerializationUtil.deserializer(data, RpcResponse.class);

        if (rpcResponse == null) {
            ctx.close();
        }
        out.add(rpcResponse);
    }
}
