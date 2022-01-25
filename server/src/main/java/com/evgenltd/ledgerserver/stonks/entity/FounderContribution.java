package com.evgenltd.ledgerserver.stonks.entity;

import com.evgenltd.ledgerserver.common.entity.Document;
import lombok.*;
import javax.persistence.*;
import java.lang.String;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.evgenltd.ledgerserver.base.entity.Account;

@Entity
@Table(name = "document_founder_contribution")
@Getter
@Setter
@NoArgsConstructor
public class FounderContribution extends Document {

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Builder
    public FounderContribution(
            final Long id,
            final String name,
            final Boolean approved,
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account
    ) {
        super(id, name, approved, date);
        this.amount = amount;
        this.account = account;
    }
}