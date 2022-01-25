package com.evgenltd.ledgerserver.stonks.entity;

import com.evgenltd.ledgerserver.common.entity.Document;
import lombok.*;
import javax.persistence.*;
import java.lang.String;
import java.math.BigDecimal;
import com.evgenltd.ledgerserver.common.entity.Account;

@Entity
@Table(name = "document_founder_contribution")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FounderContribution extends Document {

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}