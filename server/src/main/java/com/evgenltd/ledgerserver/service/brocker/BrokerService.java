package com.evgenltd.ledgerserver.service.brocker;

import com.evgenltd.ledgerserver.reference.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BrokerService {

    BigDecimal calculateCommission(Account account, LocalDateTime date, BigDecimal amount);

    BigDecimal rate(String figi);

}
