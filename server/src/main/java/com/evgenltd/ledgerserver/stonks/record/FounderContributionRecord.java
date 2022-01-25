package com.evgenltd.ledgerserver.stonks.record;

import com.evgenltd.ledgerserver.util.ReferenceRecord;

import java.math.BigDecimal;

public record FounderContributionRecord(
    Long id,
    String name,
    BigDecimal amount,
    ReferenceRecord account
) {}