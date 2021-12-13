package com.evgenltd.ledgerserver.document.common.entity;

import com.evgenltd.ledgerserver.platform.ApplicationException;
import com.evgenltd.ledgerserver.service.bot.activity.document.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Entity
@Table(name = "documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
        TRANSFER(TransferActivity.class, TypeConstant.TRANSFER),
        FOUNDER_CONTRIBUTION(FounderContributionActivity.class, TypeConstant.FOUNDER_CONTRIBUTION),
        BUY_CURRENCY(BuyCurrencyActivity.class, TypeConstant.BUY_CURRENCY),
        SELL_CURRENCY(SellCurrencyActivity.class, TypeConstant.SELL_CURRENCY),
        BUY_STOCK(BuyStockActivity.class, TypeConstant.BUY_STOCK),
        SELLS_STOCK(SellStockActivity.class, TypeConstant.SELLS_STOCK),
        BUY_CURRENCY_STOCK(BuyCurrencyStockActivity.class, TypeConstant.BUY_CURRENCY_STOCK),
        SELL_CURRENCY_STOCK(SellCurrencyStockActivity.class, TypeConstant.SELL_CURRENCY_STOCK);

        private final Class<? extends DocumentActivity> activity;
        private final String id;

        Type(final Class<? extends DocumentActivity> activity, final String id) {
            this.activity = activity;
            this.id = id;
        }

        public Class<? extends DocumentActivity> getActivity() {
            return activity;
        }

        public String id() {
            return id;
        }

        public static Type byId(final String id) {
            return Stream.of(values())
                    .filter(type -> type.id().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ApplicationException("Unknown document type [%s]", id));
        }
    }

}
