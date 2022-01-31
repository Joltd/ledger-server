package com.evgenltd.ledgerserver.base.service;

import com.evgenltd.ledgerserver.base.entity.JournalEntry;
import com.evgenltd.ledgerserver.base.record.TurnoverEntryRecord;
import com.evgenltd.ledgerserver.base.record.TurnoverRecord;
import com.evgenltd.ledgerserver.base.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.common.service.BaseService;
import com.evgenltd.ledgerserver.settings.service.SettingService;
import com.evgenltd.ledgerserver.util.filter.Filter;
import com.evgenltd.ledgerserver.util.filter.FilterConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TurnoverReportService extends BaseService {

    private final JournalEntryRepository journalEntryRepository;

    public TurnoverReportService(
            final SettingService settingService,
            final JournalEntryRepository journalEntryRepository
    ) {
        super(settingService);
        this.journalEntryRepository = journalEntryRepository;
    }

    public TurnoverRecord turnover(
            final LocalDateTime from,
            final LocalDateTime to,
            final FilterConfig filterConfig
    ) {

        final Filter.Expression wrappedWithToDate = wrapIfNecessary(filterConfig.expression(), to);
        final List<JournalEntry> data = journalEntryRepository.findAll(Filter.toSpecification(wrappedWithToDate));

        final Map<String, TurnoverEntryRecord> result = data.stream()
                .collect(Collectors.groupingBy(
                        this::getEntryCode,
                        Collectors.reducing(
                                emptyTurnover(),
                                entry -> (from != null && entry.getDate().isBefore(from))
                                        ? before(entry)
                                        : turnover(entry),
                                this::combineTurnover
                        )
                ));

        final List<TurnoverEntryRecord> turnoverEntries = result.values()
                .stream()
                .map(turnover -> {
                    final BigDecimal before = turnover.beforeDt().subtract(turnover.beforeCt());
                    final BigDecimal beforeDt = before.compareTo(BigDecimal.ZERO) > 0 ? before : BigDecimal.ZERO;
                    final BigDecimal beforeCt = before.compareTo(BigDecimal.ZERO) < 0 ? before.abs() : BigDecimal.ZERO;

                    final BigDecimal after = turnover.turnoverDt().subtract(turnover.turnoverCt()).add(before);
                    final BigDecimal afterDt = after.compareTo(BigDecimal.ZERO) > 0 ? after : BigDecimal.ZERO;
                    final BigDecimal afterCt = after.compareTo(BigDecimal.ZERO) < 0 ? after.abs() : BigDecimal.ZERO;

                    return new TurnoverEntryRecord(
                            turnover.code(),
                            beforeDt,
                            beforeCt,
                            turnover.turnoverDt(),
                            turnover.turnoverCt(),
                            afterDt,
                            afterCt
                    );
                })
                .sorted(Comparator.comparing(TurnoverEntryRecord::code))
                .collect(Collectors.toList());

        return new TurnoverRecord(
                turnoverEntries,
                turnoverEntries.stream().reduce(emptyTurnover(), this::combineTurnover)
        );
    }

    private Filter.Expression wrapIfNecessary(final Filter.Expression expression, final LocalDateTime to) {
        if (to == null) {
            return expression;
        }
        return new Filter.Expression(
                Filter.ExpressionType.AND,
                Arrays.asList(
                        new Filter.Expression("to", Filter.Operator.LESS_EQUAL, to.toString()),
                        expression
                )
        );
    }

    private String getEntryCode(final JournalEntry entry) {
        final String code = entry.getCode();
        final int point = code.indexOf(".");
        if (point < 0) {
            return code;
        }

        return code.substring(0, point);
    }

    private TurnoverEntryRecord before(final JournalEntry entry) {
        return new TurnoverEntryRecord(
                getEntryCode(entry),
                byType(JournalEntry.Type.DEBIT, entry),
                byType(JournalEntry.Type.CREDIT, entry),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    private TurnoverEntryRecord turnover(final JournalEntry entry) {
        return new TurnoverEntryRecord(
                getEntryCode(entry),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                byType(JournalEntry.Type.DEBIT, entry),
                byType(JournalEntry.Type.CREDIT, entry),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    private BigDecimal byType(final JournalEntry.Type type, final JournalEntry entry) {
        if (entry.getType().equals(type)) {
            return entry.getAmount();
        } else {
            return BigDecimal.ZERO;
        }
    }

    private TurnoverEntryRecord combineTurnover(final TurnoverEntryRecord left, final TurnoverEntryRecord right) {
        return new TurnoverEntryRecord(
                Objects.requireNonNullElse(left.code(), right.code()),
                left.beforeDt().add(right.beforeDt()),
                left.beforeCt().add(right.beforeCt()),
                left.turnoverDt().add(right.turnoverDt()),
                left.turnoverCt().add(right.turnoverCt()),
                left.afterDt().add(right.afterDt()),
                left.afterCt().add(right.afterCt())
        );
    }

    private TurnoverEntryRecord emptyTurnover() {
        return new TurnoverEntryRecord(
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

}
