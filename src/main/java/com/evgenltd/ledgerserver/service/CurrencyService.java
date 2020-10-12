package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.entity.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CurrencyService {

    public BigDecimal currencyRate(final LocalDate date, final Currency currency) {
        return BigDecimal.ZERO;
    }

}
