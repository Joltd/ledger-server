package old.ledgerserver.record;

import java.math.BigDecimal;

public record StockBalance(BigDecimal balance, BigDecimal currencyBalance, Long count) {}
