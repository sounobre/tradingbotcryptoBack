package com.dnobretech.tradingbotcrypto.controller;

import com.dnobretech.tradingbotcrypto.domain.Candle;
import com.dnobretech.tradingbotcrypto.dto.BacktestDtos;
import com.dnobretech.tradingbotcrypto.repository.CandleRepository;
import com.dnobretech.tradingbotcrypto.service.BacktestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/backtests")
@RequiredArgsConstructor
@CrossOrigin
public class BacktestController {
    private final CandleRepository repo;
    private final BacktestService backtestService;


    @PostMapping
    public BacktestDtos.BacktestResult run(@RequestBody BacktestDtos.BacktestRequest req){
        List<Candle> candles = repo.findLastN(req.symbol(), req.interval(), req.limit()==null?500:req.limit());
        java.util.Collections.reverse(candles);
        return backtestService.run(candles, req.strategy(), n(req.fast(),12), n(req.slow(),26), n(req.rsi(),14), n(req.bb(),20));
    }


    private int n(Integer val, int def){ return val==null?def:val; }
}
