package com.evgenltd.ledgerserver.reference.entity;

public enum Currency {
    RUB(""),
    USD("BBG0013HGFT4"),
    EUR("BBG0013HJJ31");

    private final String figi;

    Currency(final String figi) {
        this.figi = figi;
    }

    public String getFigi() {
        return figi;
    }
}
