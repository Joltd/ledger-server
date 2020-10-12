package com.evgenltd.ledgerserver.record;

import java.math.BigDecimal;

public record CurrencyAmount(BigDecimal amount, BigDecimal currencyAmount) {}
