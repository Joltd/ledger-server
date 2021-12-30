package com.evgenltd.ledgerserver.repository;

import com.evgenltd.ledgerserver.platform.entities.reference.account.Account;
import com.evgenltd.ledgerserver.entity.TinkoffTariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TinkoffTariffRepository extends JpaRepository<TinkoffTariff, Long> {

    Optional<TinkoffTariff> findTopByAccountAndDateLessThanOrderByDateDesc(Account account, LocalDateTime date);

}
