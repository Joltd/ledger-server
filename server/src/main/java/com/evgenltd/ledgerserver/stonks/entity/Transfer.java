package com.evgenltd.ledgerserver.stonks.entity;

import com.evgenltd.ledgerserver.common.entity.Document;
import lombok.*;
import javax.persistence.*;
import java.lang.String;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.evgenltd.ledgerserver.base.entity.Account;

@Entity
@Table(name = "document_transfer")
@Getter
@Setter
@NoArgsConstructor
public class Transfer extends Document {

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private Account from;

    @ManyToOne
    @JoinColumn(name = "to_id")
    private Account to;

    @Builder
    public Transfer(
            final Long id,
            final String name,
            final Boolean approved,
            final LocalDateTime date,
            final BigDecimal amount,
            final Account from,
            final Account to
    ) {
        super(id, name, approved, date);
        this.amount = amount;
        this.from = from;
        this.to = to;
    }

}