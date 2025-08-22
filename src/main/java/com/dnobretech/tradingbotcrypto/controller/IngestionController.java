package com.dnobretech.tradingbotcrypto.controller;

import com.dnobretech.tradingbotcrypto.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.time.Instant;
import java.util.Map;


@RestController
@RequestMapping("/api/ingest")
@RequiredArgsConstructor
@CrossOrigin
public class IngestionController {
    private final IngestionService ingestionService;


    public record Req(String symbol, String interval, Integer limit){}
    public record RangeReq(String symbol, String interval, String start, String end){}


    @PostMapping
    public Map<String,Object> ingest(@RequestBody Req req){
        int saved = ingestionService.ingestRecent(req.symbol(), req.interval(), req.limit()==null?500:req.limit());
        return Map.of("saved", saved);
    }

    @PostMapping("/range")
    public Map<String,Object> ingestRange(@RequestBody RangeReq req){
        Instant start = Instant.parse(req.start());
        Instant end = Instant.parse(req.end());
        int saved = ingestionService.ingestRange(req.symbol(), req.interval(), start, end);
        return Map.of("saved", saved);
    }
}
