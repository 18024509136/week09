package com.geek;

import org.dromara.hmily.annotation.Hmily;

public interface BankAApi {

    @Hmily
    boolean tradeInit(BankATransferDto bankATransferDto);

    @Hmily
    boolean tradeCommit(BankATransferDto bankATransferDto);

    @Hmily
    boolean tradeRollback(BankATransferDto bankATransferDto);
}
