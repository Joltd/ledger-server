package old.ledgerserver.platform.entities.document.buycurrencystock;

import java.lang.String;
import java.math.BigDecimal;
import java.lang.Long;

public record BuyCurrencyStockRow(
    Long id,
    String name,
    Long accountId,
    String accountName,
    Long tickerId,
    String tickerName,
    BigDecimal price,
    Long count,
    Long currencyId,
    String currencyName,
    BigDecimal currencyAmount
) {}