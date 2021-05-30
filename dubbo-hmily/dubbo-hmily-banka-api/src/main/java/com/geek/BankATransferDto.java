package com.geek;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BankATransferDto implements Serializable {

    private Long txId;

    Long cnyAccountId;

    BigDecimal inputFund;

    Long usdAccountId;

    BigDecimal outputFund;
}
