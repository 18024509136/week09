package com.geek;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class TradeServiceImpl implements TradeService {

    @DubboReference(check = false)
    private BankAApi bankAApi;

    @DubboReference(check = false)
    private BankBApi bankBApi;

    @HmilyTCC(confirmMethod = "exchangeConfirm", cancelMethod = "exchangeCancel")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exchangeTry(ExchangeVo exchangeVo) {
        //TODO 根据fromUserId获取fromUser的人民币账户和美元账户
        Long fromCnyAccountId = 1L;
        Long fromUsdAccountId = 11L;

        //TODO 根据toUserId获取toUser的人民币账户和美元账户
        Long toCnyAccountId = 2L;
        Long toUsdAccountId = 22L;

        BigDecimal cnyExchangeFund = exchangeVo.getExchangeFund();
        BigDecimal usdExchangeFund = exchangeVo.getExchangeFund().multiply(exchangeVo.getExchangeRate());

        log.info("============调用banka============");
        BankATransferDto bankATransferDto = new BankATransferDto();
        bankATransferDto.setTxId(exchangeVo.getTxId());
        bankATransferDto.setCnyAccountId(fromCnyAccountId);
        bankATransferDto.setInputFund(cnyExchangeFund);
        bankATransferDto.setUsdAccountId(fromUsdAccountId);
        bankATransferDto.setOutputFund(usdExchangeFund);
        bankAApi.tradeInit(bankATransferDto);

        log.info("============调用bankb============");
        BankBTransferDto bankBTransferDto = new BankBTransferDto();
        bankBTransferDto.setTxId(exchangeVo.getTxId());
        bankBTransferDto.setCnyAccountId(toCnyAccountId);
        bankBTransferDto.setOutputFund(cnyExchangeFund);
        bankBTransferDto.setUsdAccountId(toUsdAccountId);
        bankBTransferDto.setInputFund(usdExchangeFund);

        bankBApi.tradeInit(bankBTransferDto);
    }

    @Override
    public void exchangeConfirm(ExchangeVo exchangeVo) {
        log.info("============执行commit============");
    }

    @Override
    public void exchangeCancel(ExchangeVo exchangeVo) {
        log.info("============执行cancel============");
    }
}
