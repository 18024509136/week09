package com.geek;

import com.geek.dto.Order;
import com.geek.stub.OrderService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 客户端测试类
 *
 * @author huangxiaodi
 * @since 2021-05-26 16:18
 */
public class ClientApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.geek");
        // 获取orderServiceFacade对象，后续让aop去代理增强
        // OrderService orderService = applicationContext.getBean(OrderService.class);

        // 使用cglib来代理orderService接口
        RpcClientCglibProxy cglibProxy = applicationContext.getBean(RpcClientCglibProxy.class);
        OrderService orderService = cglibProxy.createProxy(OrderService.class);

        Order order = new Order();
        order.setTotalAmount(2);
        order.setTotalPrice(20000);
        String orderNo = orderService.createOrder(order);
        System.out.println("新建订单的订单号：" + orderNo);

        Order orderFind = orderService.getByOrderNo("111111");
        System.out.println("查询订单：" + orderFind);
    }
}
