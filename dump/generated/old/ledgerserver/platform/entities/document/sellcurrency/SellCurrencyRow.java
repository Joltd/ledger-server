package old.ledgerserver.platform.entities.document.sellcurrency;

import java.lang.String;
import java.math.BigDecimal;

public record SellCurrencyRow(
    Long id,
    String name,
    Long accountId,
    String accountName,
    Long currencyId,
    String currencyName,
    BigDecimal currencyRate,
    BigDecimal currencyAmount,
    Long commissionId,
    String commissionName,
    BigDecimal commissionAmount,
    Long currencySaleIncomeId,
    String currencySaleIncomeName,
    Long currencySaleExpenseId,
    String currencySaleExpenseName
) {}