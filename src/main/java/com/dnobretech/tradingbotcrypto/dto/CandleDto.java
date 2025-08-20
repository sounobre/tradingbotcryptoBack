package com.dnobretech.tradingbotcrypto.dto;

import java.math.BigDecimal;
import java.time.Instant;


public record CandleDto(
        Instant openTime, Instant closeTime,
        BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close,
        BigDecimal volume
) {}
