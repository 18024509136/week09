package com.geek;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BankBTransferDto implements Serializable {

    private Long txId;

    Long cnyAccountId;

    BigDecimal outputFund;

    Long usdAccountId;

    BigDecimal inputFund;
}
