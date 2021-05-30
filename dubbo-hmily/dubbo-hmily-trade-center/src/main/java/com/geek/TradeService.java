package com.geek;

public interface TradeService {

    void exchangeTry(ExchangeVo exchangeVo);

    void exchangeConfirm(ExchangeVo exchangeVo);

    void exchangeCancel(ExchangeVo exchangeVo);
}
