package com.evgenltd.ledgerserver.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface StockExchangeService {

    Map<StockExchangeService.Key, BigDecimal> getRateCache();

    void clearCache();

    BigDecimal rate(String ticker);

    BigDecimal rate(LocalDate date, String ticker);

    record Key(String ticker, LocalDate date) {}

}
