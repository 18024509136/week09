package com.geek;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class BankATest extends DubboHmilyBankaServiceApplicationTests {

    @Autowired
    private BankAApi bankAApi;

    @Test
    public void testInit() {
        BankATransferDto bankATransferDto = new BankATransferDto();
        bankATransferDto.setTxId(1001L);
        bankATransferDto.setCnyAccountId(1L);
        bankATransferDto.setInputFund(new BigDecimal(7));
        bankATransferDto.setUsdAccountId(11L);
        bankATransferDto.setOutputFund(new BigDecimal(1));

        bankAApi.tradeInit(bankATransferDto);
    }

    @Test
    public void testCommit() {
        BankATransferDto bankATransferDto = new BankATransferDto();
        bankATransferDto.setTxId(1001L);
        bankATransferDto.setCnyAccountId(1L);
        bankATransferDto.setInputFund(new BigDecimal(7));
        bankATransferDto.setUsdAccountId(11L);
        bankATransferDto.setOutputFund(new BigDecimal(1));

        bankAApi.tradeCommit(bankATransferDto);
    }

    @Test
    public void testRollback() {
        BankATransferDto bankATransferDto = new BankATransferDto();
        bankATransferDto.setTxId(1001L);
        bankATransferDto.setCnyAccountId(1L);
        bankATransferDto.setInputFund(new BigDecimal(7));
        bankATransferDto.setUsdAccountId(11L);
        bankATransferDto.setOutputFund(new BigDecimal(1));

        bankAApi.tradeRollback(bankATransferDto);
    }

}
