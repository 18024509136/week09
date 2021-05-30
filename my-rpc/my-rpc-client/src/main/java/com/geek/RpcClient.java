package com.geek;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

/**
 * Rpc客户端核心类
 *
 * @author huangxiaodi
 * @since 2021-05-26 09:34
 */
@Component
@ChannelHandler.Sharable
public class RpcClient extends SimpleChannelInboundHandler<Object> {

    private Object lock = new Object();

    private RpcResponse rpcResponse;

    private static final String SERVER_IP = "127.0.0.1";

    private static final int SERVER_PORT = 9000;

    /**
     * 使用netty客户端发送请求
     *
     * @param rpcRequest
     * @return
     * @throws Exception
     */
    public RpcResponse send(RpcRequest rpcRequest) throws Exception {
        NioEventLoopGroup workGroup = new NioEventLoopGroup(1);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    // out1
                    pipeline.addLast(new RpcClientEncoder());
                    // in1
                    pipeline.addLast(new RpcClientDecoder());
                    // in2
                    pipeline.addLast(RpcClient.this);
                }
            });
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            // 待优化，服务端将RpcRequest的interfaceName注册到zookeeper中。客户端通过接口名称查找服务提供者的IP和端口，并且服务提供者有多个实例的时候有负载均衡
            ChannelFuture channelFuture = bootstrap.connect(SERVER_IP, SERVER_PORT).sync();
            channelFuture.channel().writeAndFlush(rpcRequest);

            // 等待响应
            synchronized (lock) {
                lock.wait();
            }

            if (rpcResponse != null) {
                channelFuture.channel().closeFuture().sync();
            }
            return rpcResponse;

        } finally {
            workGroup.shutdownGracefully().sync();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        this.rpcResponse = (RpcResponse) msg;

        synchronized (lock) {
            // 唤醒等待响应的线程
            lock.notifyAll();
        }
        ctx.close();
    }
}
