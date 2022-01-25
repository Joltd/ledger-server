package com.evgenltd.ledgerserver.stonks.record;

public record TickerSymbolRecord(
    Long id,
    String name,
    String figi,
    Boolean withoutCommission
) {}