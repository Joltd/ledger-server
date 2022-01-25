package old.ledgerserver.service;

import com.evgenltd.ledgerserver.common.entity.JournalEntry;
import old.ledgerserver.record.CodeCard;
import old.ledgerserver.record.CodeCardEntry;
import old.ledgerserver.record.Turnover;
import com.evgenltd.ledgerserver.common.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.util.Utils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final JournalEntryRepository journalEntryRepository;

    public ReportService(final JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    public List<Turnover> turnover(
            final LocalDateTime from,
            final LocalDateTime to
    ) {
        final List<JournalEntry> data = journalEntryRepository.findByDateLessThanEqual(to);

        final Map<String, Turnover> result = data.stream()
                .collect(Collectors.groupingBy(
                        this::getEntryCode,
                        Collectors.reducing(
                                emptyTurnover(),
                                entry -> entry.getDate().isBefore(from)
                                        ? before(entry)
                                        : turnover(entry),
                                this::combineTurnover
                        )
                ));

        return result.values()
                .stream()
                .map(turnover -> {
                    final BigDecimal before = turnover.beforeDt().subtract(turnover.beforeCt());
                    final BigDecimal beforeDt = before.compareTo(BigDecimal.ZERO) > 0 ? before : BigDecimal.ZERO;
                    final BigDecimal beforeCt = before.compareTo(BigDecimal.ZERO) < 0 ? before.abs() : BigDecimal.ZERO;

                    final BigDecimal after = turnover.turnoverDt().subtract(turnover.turnoverCt()).add(before);
                    final BigDecimal afterDt = after.compareTo(BigDecimal.ZERO) > 0 ? after : BigDecimal.ZERO;
                    final BigDecimal afterCt = after.compareTo(BigDecimal.ZERO) < 0 ? after.abs() : BigDecimal.ZERO;

                    return new Turnover(
                            turnover.code(),
                            beforeDt,
                            beforeCt,
                            turnover.turnoverDt(),
                            turnover.turnoverCt(),
                            afterDt,
                            afterCt
                    );
                })
                .sorted(Comparator.comparing(Turnover::code))
                .collect(Collectors.toList());
    }

    public Turnover totalTurnover(final List<Turnover> turnover) {
        return turnover.stream().reduce(emptyTurnover(), this::combineTurnover);
    }

    public CodeCard codeCard(final String code, final LocalDateTime from, final LocalDateTime to) {
        final List<JournalEntry> data = journalEntryRepository.findByDateLessThanEqual(to);

        final CodeCardEntry before = data.stream()
                .filter(entry -> entry.getDate().isBefore(from))
                .collect(Collectors.groupingBy(
                        JournalEntry::getOperation,
                        Collectors.reducing(
                                emptyCodeCardEntry(),
                                entry -> mapToCodeCardEntry(entry, code),
                                this::combineCodeCardEntry
                        )
                ))
                .values()
                .stream()
                .filter(entry -> entry.dt().equals(code) || entry.ct().equals(code))
                .reduce(emptyCodeCardEntry(), this::combineCodeCardEntry);

        final List<CodeCardEntry> operations = data.stream()
                .filter(entry -> !entry.getDate().isBefore(from))
                .collect(Collectors.groupingBy(
                        JournalEntry::getOperation,
                        Collectors.reducing(
                                emptyCodeCardEntry(),
                                entry -> mapToCodeCardEntry(entry, code),
                                this::combineCodeCardEntry
                        )
                ))
                .values()
                .stream()
                .filter(entry -> entry.dt().equals(code) || entry.ct().equals(code))
                .sorted(Comparator.comparing(CodeCardEntry::date))
                .collect(Collectors.toList());

        final CodeCardEntry turnover = combineCodeCardEntryAmount(
                operations.stream().reduce(emptyCodeCardEntry(), this::combineCodeCardEntry)
        );

        final CodeCardEntry after = combineCodeCardEntryAmount(combineCodeCardEntry(before, turnover));

        return new CodeCard(
                before,
                operations,
                turnover,
                after
        );
    }

    // ##################################################
    // #                                                #
    // #  Turnover                                      #
    // #                                                #
    // ##################################################

    private String getEntryCode(final JournalEntry entry) {
        final String code = entry.getCode();
        final int point = code.indexOf(".");
        if (point < 0) {
            return code;
        }

        return code.substring(0, point);
    }

    private Turnover before(final JournalEntry entry) {
        return new Turnover(
                getEntryCode(entry),
                byType(JournalEntry.Type.DEBIT, entry),
                byType(JournalEntry.Type.CREDIT, entry),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    private Turnover turnover(final JournalEntry entry) {
        return new Turnover(
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

    private Turnover combineTurnover(final Turnover left, final Turnover right) {
        return new Turnover(
                Utils.ifNull(left.code(), right.code()),
                left.beforeDt().add(right.beforeDt()),
                left.beforeCt().add(right.beforeCt()),
                left.turnoverDt().add(right.turnoverDt()),
                left.turnoverCt().add(right.turnoverCt()),
                left.afterDt().add(right.afterDt()),
                left.afterCt().add(right.afterCt())
        );
    }

    private Turnover emptyTurnover() {
        return new Turnover(
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    // ##################################################
    // #                                                #
    // #  Code Card                                     #
    // #                                                #
    // ##################################################

    private CodeCardEntry mapToCodeCardEntry(final JournalEntry entry, final String code) {
        final String entryCode = getEntryCode(entry);
        return new CodeCardEntry(
                entry.getDate(),
                entry.getDocument().getComment(),
                Objects.equals(entry.getType(), JournalEntry.Type.DEBIT) ? entryCode : null,
                Objects.equals(entry.getType(), JournalEntry.Type.CREDIT) ? entryCode : null,
                Objects.equals(entry.getType(), JournalEntry.Type.DEBIT) && Objects.equals(entryCode, code) ? entry.getAmount() : BigDecimal.ZERO,
                Objects.equals(entry.getType(), JournalEntry.Type.CREDIT) && Objects.equals(entryCode, code) ? entry.getAmount() : BigDecimal.ZERO
        );
    }

    private CodeCardEntry combineCodeCardEntry(final CodeCardEntry left, final CodeCardEntry right) {
        return new CodeCardEntry(
                Utils.ifNull(left.date(), right.date()),
                Utils.ifNull(left.comment(), right.comment()),
                Utils.ifNull(left.dt(), right.dt()),
                Utils.ifNull(left.ct(), right.ct()),
                left.dtAmount().add(right.dtAmount()),
                left.ctAmount().add(right.ctAmount())
        );
    }

    private CodeCardEntry combineCodeCardEntryAmount(final CodeCardEntry entry) {
        final BigDecimal turnoverAmount = entry.dtAmount().subtract(entry.ctAmount());
        if (turnoverAmount.compareTo(BigDecimal.ZERO) > 0) {
            return new CodeCardEntry(null, null, null, null, turnoverAmount, BigDecimal.ZERO);
        }
        if (turnoverAmount.compareTo(BigDecimal.ZERO) < 0) {
            return new CodeCardEntry(null, null, null, null, BigDecimal.ZERO, turnoverAmount.abs());
        }
        return new CodeCardEntry(null, null, null, null, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private CodeCardEntry emptyCodeCardEntry() {
        return new CodeCardEntry(null, null, null, null, BigDecimal.ZERO, BigDecimal.ZERO);
    }

}
