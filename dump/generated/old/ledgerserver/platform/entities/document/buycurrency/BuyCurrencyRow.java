package old.ledgerserver.platform.entities.document.buycurrency;

import java.lang.String;
import java.math.BigDecimal;

public record BuyCurrencyRow(
    Long id,
    String name,
    BigDecimal amount,
    Long accountId,
    String accountName,
    Long currencyId,
    String currencyName,
    BigDecimal currencyRate,
    BigDecimal currencyAmount,
    Long commissionId,
    String commissionName,
    BigDecimal commissionAmount
) {}