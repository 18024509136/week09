package com.geek;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * RpcServer启动处理类
 *
 * @author huangxiaodi
 * @since 2021-05-26 11:29
 */
@Component
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final int SERVER_PORT = 9000;

    private Map<String, Object> serviceMap = new HashMap<>(10);

    /**
     * 构建服务Map，通过Map知道接口对应的实现类
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取RpcService注解的实现类
        Map<String, Object> serviceBeans = applicationContext.getBeansWithAnnotation(RpcService.class);

        // RpcService注解的值为key（即接口名），RpcService注解的实现类为value，构建服务映射map
        serviceBeans.values().forEach(serviceBean -> {
            Class<?> targetInterfaceClass = serviceBean.getClass().getAnnotation(RpcService.class).value();

            String interfaceName;
            // 如果注解的value是默认值Object，那么则获取实现类的接口
            if (Object.class.equals(targetInterfaceClass)) {
                interfaceName = serviceBean.getClass().getInterfaces()[0].getName();
            } else {
                interfaceName = targetInterfaceClass.getName();
            }

            serviceMap.put(interfaceName, serviceBean);
        });

        System.out.println("======================服务映射加载完毕======================");
    }

    @Override
    public void afterPropertiesSet() {
        startNettyServer();
    }

    /**
     * 启动netty服务
     */
    private void startNettyServer() {
        NioEventLoopGroup mainGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(2);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(mainGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    // out1
                    pipeline.addLast(new RpcServerEncoder());
                    // in1
                    pipeline.addLast(new RpcServerDecoder());
                    // in2
                    pipeline.addLast(new RpcHandler(serviceMap));
                }
            });

            serverBootstrap.option(ChannelOption.SO_BACKLOG, 150);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind(SERVER_PORT).sync();

            System.out.println("======================服务端启动成功======================");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                mainGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                workGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
