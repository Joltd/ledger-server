package com.evgenltd.ledgerserver.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface StockExchangeService {

    BigDecimal rate(String ticker);

    BigDecimal rate(LocalDate date, String ticker);

}
