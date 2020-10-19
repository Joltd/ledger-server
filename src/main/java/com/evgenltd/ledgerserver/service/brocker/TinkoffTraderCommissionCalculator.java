package com.evgenltd.ledgerserver.service.brocker;

import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.ExpenseItem;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import com.evgenltd.ledgerserver.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.service.SettingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service("TinkoffTraderCommissionCalculator")
public class TinkoffTraderCommissionCalculator implements CommissionCalculator {

    private final JournalEntryRepository journalEntryRepository;
    private final SettingService settingService;

    public TinkoffTraderCommissionCalculator(
            final JournalEntryRepository journalEntryRepository,
            final SettingService settingService
    ) {
        this.journalEntryRepository = journalEntryRepository;
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

        final ExpenseItem commission = settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM);

        final boolean hasCommissionInPeriod = journalEntryRepository.findByDateGreaterThanEqualAndDateLessThanAndCodeAndType(
                from.atStartOfDay(),
                to.atStartOfDay(),
                Codes.C91_2,
                JournalEntry.Type.DEBIT
        ).stream()
                .anyMatch(je -> Optional.ofNullable(je.getExpenseItem())
                        .map(ExpenseItem::getId)
                        .map(id -> Objects.equals(commission.getId(), id))
                        .orElse(false));

        final BigDecimal regularCommission = amount.multiply(new BigDecimal("0.0005"));
        return hasCommissionInPeriod
                ? regularCommission
                : regularCommission.add(new BigDecimal("290"));

    }

}
