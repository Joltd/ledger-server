package com.evgenltd.ledgerserver.document.entity;

import com.evgenltd.ledgerserver.service.bot.activity.document.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String content;
    private String comment;

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
