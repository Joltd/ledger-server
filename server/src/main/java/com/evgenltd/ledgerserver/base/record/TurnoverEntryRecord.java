package com.evgenltd.ledgerserver.base.record;

import java.math.BigDecimal;

public record TurnoverEntryRecord(
        String code,
        BigDecimal beforeDt,
        BigDecimal beforeCt,
        BigDecimal turnoverDt,
        BigDecimal turnoverCt,
        BigDecimal afterDt,
        BigDecimal afterCt
) {
}
