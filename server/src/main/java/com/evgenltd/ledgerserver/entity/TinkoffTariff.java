package com.evgenltd.ledgerserver.entity;

import com.evgenltd.ledgerserver.platform.entities.reference.account.Account;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tinkoff_tarifs")
public class TinkoffTariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private String tariff;

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(final Account account) {
        this.account = account;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(final String tariff) {
        this.tariff = tariff;
    }
}
