package com.evgenltd.ledgerserver.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_rates")
public class StockRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String ticker;

    private BigDecimal rate;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public String getTicker() {
        return ticker;
    }
    public void setTicker(final String ticker) {
        this.ticker = ticker;
    }

    public BigDecimal getRate() {
        return rate;
    }
    public void setRate(final BigDecimal price) {
        this.rate = price;
    }

}
