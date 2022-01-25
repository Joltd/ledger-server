package com.evgenltd.ledgerserver.stonks.record;

import java.math.BigDecimal;

public record FounderContributionRow(
    Long id,
    String name,
    BigDecimal amount,
    Long accountId,
    String accountName
) {}