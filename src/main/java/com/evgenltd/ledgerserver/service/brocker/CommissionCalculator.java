package com.evgenltd.ledgerserver.service.brocker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public interface CommissionCalculator {

    BigDecimal calculate(LocalDateTime date, BigDecimal amount);

    BigDecimal calculate(LocalDate from, LocalDate to, BigDecimal amount);

}
