package com.dnobretech.tradingbotcrypto.service;

import com.dnobretech.tradingbotcrypto.domain.Candle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;


import java.time.Duration;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StrategyService {


    public BarSeries toSeries(List<Candle> candles){
        BarSeries series = new BaseBarSeriesBuilder().withName("series").build();
        for (Candle c : candles) {
            Duration duration = Duration.between(c.getOpenTime(), c.getCloseTime());
            var numFactory = series.numFactory();
            Bar bar = new BaseBar(
                    duration,
                    c.getCloseTime(),
                    numFactory.numOf(c.getOpen()),
                    numFactory.numOf(c.getHigh()),
                    numFactory.numOf(c.getLow()),
                    numFactory.numOf(c.getClose()),
                    numFactory.numOf(c.getVolume()),
                    numFactory.numOf(0),
                    0
            );
            series.addBar(bar);
        }
        return series;
    }


    public Strategy emaCross(BarSeries series, int fast, int slow){
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        EMAIndicator emaFast = new EMAIndicator(close, fast);
        EMAIndicator emaSlow = new EMAIndicator(close, slow);
        Rule entry = new CrossedUpIndicatorRule(emaFast, emaSlow);
        Rule exit = new CrossedDownIndicatorRule(emaFast, emaSlow);
        return new BaseStrategy(entry, exit);
    }


    public Strategy rsiBoll(BarSeries series, int rsiPeriod, int bbPeriod){
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(close, rsiPeriod);
        BollingerBandsMiddleIndicator middle = new BollingerBandsMiddleIndicator(new SMAIndicator(close, bbPeriod));
        StandardDeviationIndicator sd = new StandardDeviationIndicator(close, bbPeriod);
        BollingerBandsUpperIndicator upper = new BollingerBandsUpperIndicator(middle, sd);
        BollingerBandsLowerIndicator lower = new BollingerBandsLowerIndicator(middle, sd);
        Rule entry = new UnderIndicatorRule(rsi, 30).and(new CrossedDownIndicatorRule(close, lower));
        Rule exit = new OverIndicatorRule(rsi, 70).or(new CrossedUpIndicatorRule(close, upper));
        return new BaseStrategy(entry, exit);
    }


    public Strategy macdRsi(BarSeries series, int fast, int slow, int signal, int rsiPeriod){
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        MACDIndicator macd = new MACDIndicator(close, fast, slow);
        EMAIndicator macdSignal = new EMAIndicator(macd, signal);
        RSIIndicator rsi = new RSIIndicator(close, rsiPeriod);
        Rule entry = new CrossedUpIndicatorRule(macd, macdSignal).and(new UnderIndicatorRule(rsi, 30));
        Rule exit = new CrossedDownIndicatorRule(macd, macdSignal).or(new OverIndicatorRule(rsi, 70));
        return new BaseStrategy(entry, exit);
    }
}
