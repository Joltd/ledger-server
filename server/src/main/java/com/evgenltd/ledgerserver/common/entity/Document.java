package com.evgenltd.ledgerserver.common.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Document extends Reference {

    private Boolean approved;

    private LocalDateTime date;

    public Document(final Long id, final String name, final Boolean approved, final LocalDateTime date) {
        super(id, name);
        this.approved = approved;
        this.date = date;
    }
}
