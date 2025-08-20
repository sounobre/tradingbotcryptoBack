package com.dnobretech.tradingbotcrypto.repository;

import com.dnobretech.tradingbotcrypto.domain.Candle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.Instant;
import java.util.List;


public interface CandleRepository extends JpaRepository<Candle, Long> {
    @Query("select c from Candle c where c.symbol = :symbol and c.interval = :interval order by c.openTime asc")
    List<Candle> findBySymbolAndIntervalOrdered(String symbol, String interval);


    @Query("select c from Candle c where c.symbol = :symbol and c.interval = :interval and c.openTime >= :start and c.openTime <= :end order by c.openTime asc")
    List<Candle> findRange(String symbol, String interval, Instant start, Instant end);


    @Query(value="select * from candles where symbol = :symbol and interval = :interval order by open_time desc limit :limit", nativeQuery = true)
    List<Candle> findLastN(String symbol, String interval, int limit);
}
