package old.ledgerserver.record;

import java.math.BigDecimal;

public record CurrencyBalance(BigDecimal balance, BigDecimal currencyAmount) {}
