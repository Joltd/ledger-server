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

    private String comment;

    public Document(final Long id, final String name, final Boolean approved, final LocalDateTime date, final String comment) {
        super(id, name);
        this.approved = approved;
        this.date = date;
    }
}
