package com.dnobretech.tradingbotcrypto.service;

import com.dnobretech.tradingbotcrypto.client.BinanceClient;
import com.dnobretech.tradingbotcrypto.domain.Candle;
import com.dnobretech.tradingbotcrypto.repository.CandleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.List;


@Service
@RequiredArgsConstructor
public class IngestionService {
    private final BinanceClient binance;
    private final CandleRepository repo;


    @Transactional
    public int ingestRecent(String symbol, String interval, int limit){
        List<Candle> fetched = binance.fetchRecent(symbol, interval, limit, null, null);
        int saved = 0;
        for (Candle c : fetched) {
            try {
                repo.save(c);
                saved++;
            } catch (DataIntegrityViolationException e){
// duplicate unique(symbol, interval, open_time): ignore
            }
        }
        return saved;
    }

    @Transactional
    public int ingestRange(String symbol, String interval, Instant start, Instant end){
        final int limit = 1000;
        int saved = 0;
        Instant cursor = start;
        while (cursor.isBefore(end)){
            List<Candle> fetched = binance.fetchRecent(symbol, interval, limit, cursor, end);
            if (fetched.isEmpty()) break;
            for (Candle c : fetched){
                try {
                    repo.save(c);
                    saved++;
                } catch (DataIntegrityViolationException e){
                    // ignore duplicates
                }
            }
            Instant lastClose = fetched.get(fetched.size()-1).getCloseTime();
            cursor = lastClose.plusMillis(1);
        }
        return saved;
    }
}
