package com.dnobretech.tradingbotcrypto.service;

import com.dnobretech.tradingbotcrypto.domain.Candle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverboughtRule;
import org.ta4j.core.rules.OversoldRule;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StrategyService {


    public BarSeries toSeries(List<Candle> candles){
        BarSeries series = new BaseBarSeriesBuilder().withName("series").build();
        for (Candle c : candles) {
            ZonedDateTime et = ZonedDateTime.ofInstant(c.getCloseTime(), ZoneOffset.UTC);
            series.addBar(et,
                    c.getOpen().doubleValue(),
                    c.getHigh().doubleValue(),
                    c.getLow().doubleValue(),
                    c.getClose().doubleValue(),
                    c.getVolume().doubleValue());
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
        BollingerBandsStandardDeviationIndicator sd = new BollingerBandsStandardDeviationIndicator(close, bbPeriod);
        BollingerBandsUpperIndicator upper = new BollingerBandsUpperIndicator(middle, sd);
        BollingerBandsLowerIndicator lower = new BollingerBandsLowerIndicator(middle, sd);
        Rule entry = new OversoldRule(rsi, 30).and(new CrossedDownIndicatorRule(close, lower));
        Rule exit = new OverboughtRule(rsi, 70).or(new CrossedUpIndicatorRule(close, upper));
        return new BaseStrategy(entry, exit);
    }
}
