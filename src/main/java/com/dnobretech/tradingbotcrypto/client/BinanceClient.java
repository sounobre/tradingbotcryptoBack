package com.dnobretech.tradingbotcrypto.client;

import com.dnobretech.tradingbotcrypto.domain.Candle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class BinanceClient {
    private final HttpClient http = HttpClient.newHttpClient();


    public List<Candle> fetchRecent(String symbol, String interval, int limit, Instant startTime, Instant endTime) {
        try {
            StringBuilder url = new StringBuilder(String.format(
                    "https://api.binance.com/api/v3/klines?symbol=%s&interval=%s", symbol, interval));
            if (limit > 0) url.append("&limit=").append(limit);
            if (startTime != null) url.append("&startTime=").append(startTime.toEpochMilli());
            if (endTime != null) url.append("&endTime=").append(endTime.toEpochMilli());
            HttpRequest req = HttpRequest.newBuilder(URI.create(url.toString())).GET().build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) throw new RuntimeException("Binance error: " + res.statusCode());
            // Response is an array of arrays; we parse manually to avoid extra deps
            return parseKlinesJson(res.body(), symbol, interval);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private List<Candle> parseKlinesJson(String json, String symbol, String interval) {
// Very small JSON parser assuming Binance array-of-arrays format
// [ [ openTime, open, high, low, close, volume, closeTime, ... ], ... ]
        List<Candle> list = new ArrayList<>();
        String s = json.trim();
        if (s.length() < 5) return list;
        int idx = 0;
        for (String row : s.substring(1, s.length()-1).split("\\],\\[")) {
            String r = row.replace("[", "").replace("]", "");
            String[] parts = r.split(",");
            long openTime = Long.parseLong(parts[0]);
            Instant ot = Instant.ofEpochMilli(openTime);
            Instant ct = Instant.ofEpochMilli(Long.parseLong(parts[6]));
            BigDecimal open = new BigDecimal(strip(parts[1]));
            BigDecimal high = new BigDecimal(strip(parts[2]));
            BigDecimal low = new BigDecimal(strip(parts[3]));
            BigDecimal close= new BigDecimal(strip(parts[4]));
            BigDecimal vol = new BigDecimal(strip(parts[5]));
            list.add(Candle.builder()
                    .symbol(symbol).interval(interval)
                    .openTime(ot).closeTime(ct)
                    .open(open).high(high).low(low).close(close)
                    .volume(vol).build());
            idx++;
        }
        return list;
    }
    private String strip(String v){
        String t = v.trim();
        if (t.startsWith("\"")) t = t.substring(1);
        if (t.endsWith("\"")) t = t.substring(0, t.length()-1);
        return t;
    }
}
