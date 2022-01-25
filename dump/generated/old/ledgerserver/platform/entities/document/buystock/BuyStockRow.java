package old.ledgerserver.platform.entities.document.buystock;

import java.lang.String;
import java.math.BigDecimal;
import java.lang.Long;

public record BuyStockRow(
    Long id,
    String name,
    BigDecimal amount,
    Long accountId,
    String accountName,
    Long tickerId,
    String tickerName,
    BigDecimal price,
    Long count,
    Long commissionId,
    String commissionName,
    BigDecimal commissionAmount
) {}