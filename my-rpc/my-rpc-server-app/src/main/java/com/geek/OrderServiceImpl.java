package com.geek;

import com.geek.dto.Order;
import com.geek.stub.OrderService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 订单服务实现类
 *
 * @author huangxiaodi
 * @since 2021-05-26 15:53
 */
@RpcService
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public String createOrder(Order order) {
        String orderNo = UUID.randomUUID().toString().replace("-", "");
        System.out.println("===================创建订单，订单号为：" + orderNo + " ====================");
        return orderNo;
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        return new Order(orderNo, 3, 200000);
    }
}
