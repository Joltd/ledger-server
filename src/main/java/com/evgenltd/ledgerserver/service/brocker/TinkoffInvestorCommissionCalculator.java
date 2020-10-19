package com.evgenltd.ledgerserver.service.brocker;

import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.service.SettingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service("TinkoffInvestorCommissionCalculator")
public class TinkoffInvestorCommissionCalculator implements CommissionCalculator {

    private final SettingService settingService;

    public TinkoffInvestorCommissionCalculator(final SettingService settingService) {
        this.settingService = settingService;
    }

    @Override
    public BigDecimal calculate(final LocalDateTime date, final BigDecimal amount) {
        final Integer settlementDay = settingService.get(Settings.BROKER_COMMISSION_SETTLEMENT_DAY);

        LocalDate from;
        LocalDate to;

        if (date.getDayOfMonth() >= settlementDay) {
            from = LocalDate.of(date.getYear(), date.getMonth(), settlementDay);
            to = LocalDate.of(date.getYear(), date.plusMonths(1L).getMonth(), settlementDay);
        } else {
            from = LocalDate.of(date.getYear(), date.minusMonths(1L).getMonth(), settlementDay);
            to = LocalDate.of(date.getYear(), date.getMonth(), settlementDay);
        }

        return calculate(from, to, amount);
    }

    @Override
    public BigDecimal calculate(final LocalDate from, final LocalDate to, final BigDecimal amount) {

        return null;
    }

}
