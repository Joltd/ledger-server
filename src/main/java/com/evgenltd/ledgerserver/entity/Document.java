package com.evgenltd.ledgerserver.entity;


import com.evgenltd.ledgerserver.service.bot.activity.document.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String content;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(final LocalDateTime date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public enum Type {
        TRANSFER(TransferActivity.class),
        FOUNDER_CONTRIBUTION(FounderContributionActivity.class),
        BUY_CURRENCY(BuyCurrencyActivity.class),
        SELL_CURRENCY(SellCurrencyActivity.class),
        BUY_STOCK(BuyStockActivity.class),
        SELLS_STOCK(SellStockActivity.class),
        BUY_CURRENCY_STOCK(BuyCurrencyStockActivity.class),
        SELL_CURRENCY_STOCK(SellCurrencyStockActivity.class);

        private final Class<? extends DocumentActivity> activity;

        Type(final Class<? extends DocumentActivity> activity) {
            this.activity = activity;
        }

        public Class<? extends DocumentActivity> getActivity() {
            return activity;
        }
    }

}
