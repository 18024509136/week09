package com.geek.stub;

import com.geek.dto.Order;

/**
 * 订单服务接口
 */
public interface OrderService {

    String createOrder(Order order);

    Order getByOrderNo(String orderNo);
}
