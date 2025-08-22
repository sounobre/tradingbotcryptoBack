package com.dnobretech.tradingbotcrypto.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


public class BacktestDtos {
    public record BacktestRequest(
            String symbol,
            String interval,
            Integer limit,
            String strategy,
            Integer fast,   // EMA/MACD fast period
            Integer slow,   // EMA/MACD slow period
            Integer signal, // MACD signal period
            Integer rsi,
            Integer bb,
            BigDecimal initialCapital,
            BigDecimal positionSize
    ) {}
    public record TradePoint(Instant time, String type, BigDecimal price) {}
    public record EquityPoint(Instant time, BigDecimal value) {}
    public record Metrics(BigDecimal totalReturnPct, BigDecimal maxDrawdownPct, BigDecimal winRatePct, BigDecimal profitFactor, int trades) {}
    public record BacktestResult(Metrics metrics, List<EquityPoint> equity, List<TradePoint> trades) {}
}
