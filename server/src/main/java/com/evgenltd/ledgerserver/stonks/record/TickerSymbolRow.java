package com.evgenltd.ledgerserver.stonks.record;

public record TickerSymbolRow(
    Long id,
    String name,
    String figi,
    Boolean withoutCommission
) {}