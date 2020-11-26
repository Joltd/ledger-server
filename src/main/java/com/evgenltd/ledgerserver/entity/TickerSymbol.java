package com.evgenltd.ledgerserver.entity;

import javax.persistence.*;

@Entity
@Table(name = "ticker_symbols")
public class TickerSymbol implements Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String figi;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFigi() {
        return figi;
    }

    public void setFigi(final String figi) {
        this.figi = figi;
    }

    @Override
    public String toString() {
        return String.format("%s,%s", id, name);
    }

}
