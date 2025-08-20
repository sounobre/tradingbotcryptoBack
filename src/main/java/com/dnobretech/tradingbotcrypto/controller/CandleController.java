package com.dnobretech.tradingbotcrypto.controller;

import com.dnobretech.tradingbotcrypto.domain.Candle;
import com.dnobretech.tradingbotcrypto.dto.CandleDto;
import com.dnobretech.tradingbotcrypto.repository.CandleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/candles")
@RequiredArgsConstructor
@CrossOrigin
public class CandleController {
    private final CandleRepository repo;


    @GetMapping
    public List<CandleDto> list(@RequestParam String symbol, @RequestParam String interval, @RequestParam(defaultValue="500") int limit){
        List<Candle> list = repo.findLastN(symbol, interval, limit);
// reverse to ascending by time
        java.util.Collections.reverse(list);
        return list.stream().map(c-> new CandleDto(c.getOpenTime(), c.getCloseTime(), c.getOpen(), c.getHigh(), c.getLow(), c.getClose(), c.getVolume())).collect(Collectors.toList());
    }
}
