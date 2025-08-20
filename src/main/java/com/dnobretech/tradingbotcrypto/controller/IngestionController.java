package com.dnobretech.tradingbotcrypto.controller;

import com.dnobretech.tradingbotcrypto.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.Map;


@RestController
@RequestMapping("/api/ingest")
@RequiredArgsConstructor
@CrossOrigin
public class IngestionController {
    private final IngestionService ingestionService;


    public record Req(String symbol, String interval, Integer limit){}


    @PostMapping
    public Map<String,Object> ingest(@RequestBody Req req){
        int saved = ingestionService.ingestRecent(req.symbol(), req.interval(), req.limit()==null?500:req.limit());
        return Map.of("saved", saved);
    }
}
