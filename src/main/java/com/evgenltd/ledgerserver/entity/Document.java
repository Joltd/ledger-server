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

    public enum Type {
        TRANSFER(TransferActivity.class),
        BUY_CURRENCY(BuyCurrencyActivity.class),
        BUY_STOCK(BuyStockActivity.class),
        BUY_CURRENCY_STOCK(BuyCurrencyStockActivity.class);

        private final Class<? extends DocumentActivity> activity;

        Type(final Class<? extends DocumentActivity> activity) {
            this.activity = activity;
        }

        public Class<? extends DocumentActivity> getActivity() {
            return activity;
        }
    }

}
