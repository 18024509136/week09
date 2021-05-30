package com.geek;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/trade")
@RestController
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @PostMapping("/exchange")
    public String exchange(@RequestBody ExchangeVo exchangeVo) {
        tradeService.exchangeTry(exchangeVo);
        return "ok";
    }
}
