package com.geek;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * cglib方法拦截类
 *
 * @author huangxiaodi
 * @since 2021-06-01 10:19
 */
@Component
public class ServiceFacade implements MethodInterceptor {

    @Autowired
    private RpcClient rpcClient;

    /**
     * @param obj    被代理的对象
     * @param method 被代理的方法对象
     * @param args   被代理的方法入参
     * @param proxy  生成的代理类的方法
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 获取接口名称
        String interfaceName = obj.getClass().getInterfaces()[0].getName();
        // 获取方法名
        String methodName = method.getName();
        // 获取方法入参类型数组
        Class<?>[] parameterTypes = method.getParameterTypes();

        RpcRequest rpcRequest = new RpcRequest(interfaceName, methodName, parameterTypes, args);
        // 通过netty客户端发送请求到服务端并获取响应结果
        RpcResponse response;
        try {
            response = rpcClient.send(rpcRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (!response.getCode().equals(ResponseEnum.SUCCESS.getCode())) {
            throw new RuntimeException(response.getMessage());
        }

        return response.getResult();
    }
}
