package old.ledgerserver.platform.entities.document.buystock;

import com.evgenltd.ledgerserver.util.ReferenceRecord;
import java.lang.String;
import java.math.BigDecimal;
import java.lang.Long;

public record BuyStockRecord(
    Long id,
    String name,
    BigDecimal amount,
    ReferenceRecord account,
    ReferenceRecord ticker,
    BigDecimal price,
    Long count,
    ReferenceRecord commission,
    BigDecimal commissionAmount
) {}