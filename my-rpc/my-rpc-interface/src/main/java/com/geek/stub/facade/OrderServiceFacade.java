package com.geek.stub.facade;

import com.geek.dto.Order;
import com.geek.stub.OrderService;
import org.springframework.stereotype.Service;

/**
 * 接口的空实现，以便交予spring管理和实现aop
 *
 * @author huangxiaodi
 * @since 2021-05-26 11:03
 */
@Service
public class OrderServiceFacade implements OrderService {
    @Override
    public String createOrder(Order order) {
        return null;
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        return null;
    }
}
