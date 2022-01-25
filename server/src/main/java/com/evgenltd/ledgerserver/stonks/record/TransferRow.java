package com.evgenltd.ledgerserver.stonks.record;

import java.math.BigDecimal;

public record TransferRow(
    Long id,
    String name,
    BigDecimal amount,
    Long fromId,
    String fromName,
    Long toId,
    String toName
) {}