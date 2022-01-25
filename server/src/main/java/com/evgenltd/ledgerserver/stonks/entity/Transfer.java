package com.evgenltd.ledgerserver.stonks.entity;

import com.evgenltd.ledgerserver.common.entity.Document;
import lombok.*;
import javax.persistence.*;
import java.lang.String;
import java.math.BigDecimal;
import com.evgenltd.ledgerserver.common.entity.Account;

@Entity
@Table(name = "document_transfer")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer extends Document {

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private Account from;

    @ManyToOne
    @JoinColumn(name = "to_id")
    private Account to;

}