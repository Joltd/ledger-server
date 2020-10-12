package com.evgenltd.ledgerserver.builder;

import com.evgenltd.ledgerserver.entity.JournalEntry;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class JournalEntryBuilder {

    public static JournalEntry debit(final LocalDateTime date, final String code, final BigDecimal amount) {
        return of(date, JournalEntry.Type.DEBIT, code, amount);
    }

    public static JournalEntry credit(final LocalDateTime date, final String code, final BigDecimal amount) {
        return of(date, JournalEntry.Type.CREDIT, code, amount);
    }

    private static JournalEntry of(
            final LocalDateTime date,
            final JournalEntry.Type type,
            final String code,
            final BigDecimal amount
    ) {
        final JournalEntry journalEntry = new JournalEntry();
        journalEntry.setDate(date);
        journalEntry.setType(type);
        journalEntry.setCode(code);
        journalEntry.setAmount(amount);
        return journalEntry;
    }

}
