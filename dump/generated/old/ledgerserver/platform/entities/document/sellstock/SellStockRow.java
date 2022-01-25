package old.ledgerserver.platform.entities.document.sellstock;

import java.lang.String;
import java.math.BigDecimal;
import java.lang.Long;

public record SellStockRow(
    Long id,
    String name,
    Long accountId,
    String accountName,
    Long tickerId,
    String tickerName,
    BigDecimal price,
    Long count,
    Long commissionId,
    String commissionName,
    BigDecimal commissionAmount,
    Long stockSaleIncomeId,
    String stockSaleIncomeName,
    Long stockSaleExpenseId,
    String stockSaleExpenseName
) {}