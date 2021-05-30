package com.geek;

import org.dromara.hmily.annotation.Hmily;

public interface BankBApi {

    @Hmily
    boolean tradeInit(BankBTransferDto bankATransferDto);

    @Hmily
    boolean tradeCommit(BankBTransferDto bankATransferDto);

    @Hmily
    boolean tradeRollback(BankBTransferDto bankATransferDto);
}
