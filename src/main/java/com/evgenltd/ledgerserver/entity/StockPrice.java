package com.evgenltd.ledgerserver.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_prices")
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String ticker;

    private BigDecimal price;

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

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

}
