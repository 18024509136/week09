package com.geek;

import com.geek.dto.Order;
import com.geek.stub.OrderService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * 客户端代理使用AOP的方式实现
 *
 * @author huangxiaodi
 * @since 2021-05-26 10:19
 */
@EnableAspectJAutoProxy
@Component
@Aspect
public class RpcClientAopProxy {

    @Autowired
    private RpcClient rpcClient;

    /**
     * 切入接口的所有的空实现类
     */
    @Pointcut("execution(* com.geek.stub.facade.*.*(..))")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws RuntimeException {
        // 获取接口名称
        String interfaceName = joinPoint.getTarget().getClass().getInterfaces()[0].getName();
        // 获取方法入参数组
        Object[] args = joinPoint.getArgs();
        //获取方法签名对象
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 获取方法名
        String methodName = methodSignature.getMethod().getName();
        // 获取方法参数类型数组
        Class[] paramTypes = methodSignature.getParameterTypes();

        RpcRequest rpcRequest = new RpcRequest(interfaceName, methodName, paramTypes, args);
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

    /**
     * 测试切面是否生效
     *
     * @param args
     */
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.geek");
        OrderService bean = applicationContext.getBean(OrderService.class);
        Order order = bean.getByOrderNo("1");
        System.out.println(order);
    }
}
