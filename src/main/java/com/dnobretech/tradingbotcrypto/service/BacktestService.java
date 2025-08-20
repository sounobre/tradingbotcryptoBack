package com.dnobretech.tradingbotcrypto.service;

import com.dnobretech.tradingbotcrypto.domain.Candle;
import com.dnobretech.tradingbotcrypto.dto.BacktestDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;
import org.ta4j.core.Position;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.backtest.BarSeriesManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BacktestService {
    private final StrategyService strategyService;


    public BacktestDtos.BacktestResult run(List<Candle> candles, String strategyName, int fast, int slow, int rsi, int bb){
        BarSeries series = strategyService.toSeries(candles);
        Strategy strategy = "rsi_boll".equals(strategyName) ?
                strategyService.rsiBoll(series, rsi, bb) :
                strategyService.emaCross(series, fast, slow);


        BarSeriesManager mgr = new BarSeriesManager(series);
        TradingRecord record = mgr.run(strategy);


// equity curve (simples, 1 unidade por trade, sem alavancagem)
        BigDecimal equity = BigDecimal.valueOf(10000);
        BigDecimal peak = equity;
        BigDecimal maxDD = BigDecimal.ZERO;
        List<BacktestDtos.EquityPoint> equityPoints = new ArrayList<>();
        List<BacktestDtos.TradePoint> trades = new ArrayList<>();


        int wins=0; int losses=0; BigDecimal grossProfit=BigDecimal.ZERO; BigDecimal grossLoss=BigDecimal.ZERO;


        for (Position p : record.getPositions()) {
            int entryIndex = p.getEntry().getIndex();
            int exitIndex = p.getExit().getIndex();
            double entryPrice = series.getBar(entryIndex).getClosePrice().doubleValue();
            double exitPrice = series.getBar(exitIndex).getClosePrice().doubleValue();
            BigDecimal pnlPct = BigDecimal.valueOf((exitPrice - entryPrice) / entryPrice * 100);
            if (pnlPct.compareTo(BigDecimal.ZERO) >= 0) { wins++; grossProfit = grossProfit.add(pnlPct); }
            else { losses++; grossLoss = grossLoss.add(pnlPct.abs()); }
            equity = equity.multiply(BigDecimal.ONE.add(pnlPct.divide(BigDecimal.valueOf(100))));
            if (equity.compareTo(peak) > 0) peak = equity;
            BigDecimal dd = peak.compareTo(BigDecimal.ZERO)>0 ? peak.subtract(equity).divide(peak, 8, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
            if (dd.compareTo(maxDD) > 0) maxDD = dd;
            java.time.Instant time = series.getBar(exitIndex).getEndTime();
            trades.add(new BacktestDtos.TradePoint(time, exitPrice>=entryPrice?"SELL":"SELL", BigDecimal.valueOf(exitPrice)));
            equityPoints.add(new BacktestDtos.EquityPoint(time, equity));
        }


        BigDecimal totalReturnPct = equity.subtract(BigDecimal.valueOf(10000)).divide(BigDecimal.valueOf(100), 8, java.math.RoundingMode.HALF_UP);
        BigDecimal winRatePct = record.getPositionCount() > 0 ? BigDecimal.valueOf((wins * 100.0) / record.getPositionCount()) : BigDecimal.ZERO;
        BigDecimal profitFactor = grossLoss.compareTo(BigDecimal.ZERO)>0 ? grossProfit.divide(grossLoss, 8, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal maxDrawdownPct = maxDD.multiply(BigDecimal.valueOf(100));


        BacktestDtos.Metrics metrics = new BacktestDtos.Metrics(totalReturnPct, maxDrawdownPct, winRatePct, profitFactor, record.getPositionCount());
        return new BacktestDtos.BacktestResult(metrics, equityPoints, trades);
    }
}
