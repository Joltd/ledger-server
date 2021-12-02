package com.evgenltd.ledgerserver.record;

import java.math.BigDecimal;

public final record Turnover(
        String code,
        BigDecimal beforeDt,
        BigDecimal beforeCt,
        BigDecimal turnoverDt,
        BigDecimal turnoverCt,
        BigDecimal afterDt,
        BigDecimal afterCt
) {}
