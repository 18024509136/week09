package com.geek;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeVo {

    // 交易第一对象
    private Long fromUserId;

    // 交易第二对象
    private Long toUserId;

    // 交易号
    private Long txId;

    // 交易金额，人民币
    private BigDecimal exchangeFund;

    // 美元汇率
    private BigDecimal exchangeRate;

}
