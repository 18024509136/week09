package com.geek;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 服务端业务处理inBound类
 *
 * @author huangxiaodi
 * @since 2021-05-26 15:21
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private Map<String, Object> serviceMap;

    public RpcHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
        RpcResponse rpcResponse = handler(msg);
        ctx.writeAndFlush(rpcResponse);
    }

    /**
     * 通过请求的接口名称，反射获取实现类，调用业务逻辑后封装为RcpResponse返回
     *
     * @param rpcRequest
     * @return
     */
    private RpcResponse handler(RpcRequest rpcRequest) {
        try {
            Object serviceObj = serviceMap.get(rpcRequest.getInterfaceName());
            Class<?> serviceClass = serviceObj.getClass();
            Method method = serviceClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object result = method.invoke(serviceObj, rpcRequest.getParams());
            return RpcResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return RpcResponse.error(e.getMessage());
        }
    }


}
