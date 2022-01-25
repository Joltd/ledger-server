package com.evgenltd.ledgerserver.stonks.entity;

import com.evgenltd.ledgerserver.common.entity.Reference;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ticker_symbols")
@Getter
@Setter
@NoArgsConstructor
public class TickerSymbol extends Reference {

    private String figi;

    private Boolean withoutCommission;

    @Builder
    public TickerSymbol(final Long id, final String name, final String figi, final Boolean withoutCommission) {
        super(id, name);
        this.figi = figi;
        this.withoutCommission = withoutCommission;
    }

}