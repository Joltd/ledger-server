package com.evgenltd.ledgerserver.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface StockExchangeService {

    BigDecimal rate(LocalDate date, String ticker);

}
