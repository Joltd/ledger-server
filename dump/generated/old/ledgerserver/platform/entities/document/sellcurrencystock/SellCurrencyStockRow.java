package old.ledgerserver.platform.entities.document.sellcurrencystock;

import java.lang.String;
import java.math.BigDecimal;
import java.lang.Long;
import java.lang.Boolean;

public record SellCurrencyStockRow(
    Long id,
    String name,
    Long accountId,
    String accountName,
    Long tickerId,
    String tickerName,
    BigDecimal price,
    Long count,
    BigDecimal currencyRate,
    Long currencyId,
    String currencyName,
    Long commissionId,
    String commissionName,
    BigDecimal commissionAmount,
    Long stockSaleIncomeId,
    String stockSaleIncomeName,
    Long stockSaleExpenseId,
    String stockSaleExpenseName,
    Boolean directSelling
) {}