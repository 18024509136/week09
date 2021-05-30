package com.geek;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class BankBTest extends DubboHmilyBankbServiceApplicationTests {

    @Autowired
    private BankBApi bankBApi;

    @Test
    public void testInit() {
        BankBTransferDto bankBTransferDto = new BankBTransferDto();
        bankBTransferDto.setTxId(1001L);
        bankBTransferDto.setCnyAccountId(2L);
        bankBTransferDto.setOutputFund(new BigDecimal(7));
        bankBTransferDto.setUsdAccountId(22L);
        bankBTransferDto.setInputFund(new BigDecimal(1));

        bankBApi.tradeInit(bankBTransferDto);
    }

    @Test
    public void testCommit() {
        BankBTransferDto bankBTransferDto = new BankBTransferDto();
        bankBTransferDto.setTxId(1001L);
        bankBTransferDto.setCnyAccountId(2L);
        bankBTransferDto.setOutputFund(new BigDecimal(7));
        bankBTransferDto.setUsdAccountId(22L);
        bankBTransferDto.setInputFund(new BigDecimal(1));

        bankBApi.tradeCommit(bankBTransferDto);
    }

    @Test
    public void testRollback() {
        BankBTransferDto bankBTransferDto = new BankBTransferDto();
        bankBTransferDto.setTxId(1001L);
        bankBTransferDto.setCnyAccountId(2L);
        bankBTransferDto.setOutputFund(new BigDecimal(7));
        bankBTransferDto.setUsdAccountId(22L);
        bankBTransferDto.setInputFund(new BigDecimal(1));

        bankBApi.tradeRollback(bankBTransferDto);
    }

}
