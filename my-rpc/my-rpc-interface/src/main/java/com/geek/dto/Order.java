package com.geek.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单DTO
 *
 * @author huangxiaodi
 * @since 2021-05-25 16:51
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    /**
     * 订单编码
     */
    private String orderNo;

    /**
     * 物品数量
     */
    private Integer totalAmount;

    /**
     * 总价格
     */
    private Integer totalPrice;
}
