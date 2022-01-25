package com.evgenltd.ledgerserver.common.service;

import com.evgenltd.ledgerserver.base.entity.*;
import com.evgenltd.ledgerserver.common.entity.*;
import com.evgenltd.ledgerserver.common.repository.ReferenceRepository;
import com.evgenltd.ledgerserver.base.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.settings.service.SettingService;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DocumentService<T extends Document, D, W> extends ReferenceService<T, D, W> {

    protected final JournalEntryRepository journalEntryRepository;

    private final ThreadLocal<Context> context = new ThreadLocal<>();

    public DocumentService(
            final SettingService settingService,
            final ReferenceRepository<T> referenceRepository,
            final JournalEntryRepository journalEntryRepository
    ) {
        super(settingService, referenceRepository);
        this.journalEntryRepository = journalEntryRepository;
    }

    @Override
    public void update(final D record) {
        final T entity = toEntity(record);
        if (entity.getId() != null) {
            journalEntryRepository.deleteByDocumentId(entity.getId());
        }
        referenceRepository.save(entity);

        if (entity.getApproved()) {
            final Context local = new Context(entity);
            context.set(local);
            approve(entity);
            journalEntryRepository.saveAll(local.getEntries());
            context.remove();
        }
    }

    @Override
    public void delete(final Long id) {
        journalEntryRepository.deleteByDocumentId(id);
        super.delete(id);
    }

    protected void approve(final T entity) {}

    protected void dt51(final BigDecimal amount, final Account account) {
        final JournalEntry entry = create(JournalEntry.Type.DEBIT, Codes.C51, amount);
        entry.setAccount(account);
    }

    protected void ct51(final BigDecimal amount, final Account account) {
        final JournalEntry entry = create(JournalEntry.Type.CREDIT, Codes.C51, amount);
        entry.setAccount(account);
    }

    protected void dt52(
            final BigDecimal amount,
            final Account account,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(JournalEntry.Type.DEBIT, Codes.C52, amount);
        entry.setAccount(account);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    protected void ct52(
            final BigDecimal amount,
            final Account account,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(JournalEntry.Type.CREDIT, Codes.C52, amount);
        entry.setAccount(account);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    protected void dt58(
            final BigDecimal amount,
            final Account account,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(JournalEntry.Type.DEBIT, Codes.C58, amount);
        entry.setAccount(account);
        entry.setTickerSymbol(ticker);
        entry.setPrice(price);
        entry.setCount(count);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    protected void ct58(
            final BigDecimal amount,
            final Account account,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(JournalEntry.Type.CREDIT, Codes.C58, amount);
        entry.setAccount(account);
        entry.setTickerSymbol(ticker);
        entry.setPrice(price);
        entry.setCount(count);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    protected void dt75(final BigDecimal amount) {
        create(JournalEntry.Type.DEBIT, Codes.C75, amount);
    }

    protected void ct75(final BigDecimal amount) {
        create(JournalEntry.Type.CREDIT, Codes.C75, amount);
    }

    protected void dt76(final BigDecimal amount, final Person person) {
        final JournalEntry entry = create(JournalEntry.Type.DEBIT, Codes.C76, amount);
        entry.setPerson(person);
    }

    protected void ct76(final BigDecimal amount, final Person person) {
        final JournalEntry entry = create(JournalEntry.Type.CREDIT, Codes.C76, amount);
        entry.setPerson(person);
    }

    protected void dt80(final BigDecimal amount) {
        create(JournalEntry.Type.DEBIT, Codes.C80, amount);
    }

    protected void ct80(final BigDecimal amount) {
        create(JournalEntry.Type.CREDIT, Codes.C80, amount);
    }

    protected void dt91(
            final BigDecimal amount,
            final Account account,
            final TickerSymbol ticker,
            final Currency currency,
            final ExpenseItem expenseItem
    ) {
        final JournalEntry entry = create(JournalEntry.Type.DEBIT, Codes.C91_2, amount);
        entry.setAccount(account);
        entry.setTickerSymbol(ticker);
        entry.setCurrency(currency);
        entry.setExpenseItem(expenseItem);
    }

    protected void ct91(
            final BigDecimal amount,
            final Account account,
            final TickerSymbol tickerSymbol,
            final Currency currency,
            final IncomeItem incomeItem
    ) {
        final JournalEntry entry = create(JournalEntry.Type.CREDIT, Codes.C91_1, amount);
        entry.setAccount(account);
        entry.setTickerSymbol(tickerSymbol);
        entry.setCurrency(currency);
        entry.setIncomeItem(incomeItem);
    }

    private JournalEntry create(
            final JournalEntry.Type type,
            final String code,
            final BigDecimal amount
    ) {
        final Context local = context.get();
        final JournalEntry journalEntry = new JournalEntry();
        journalEntry.setDate(local.getDocument().getDate());
        journalEntry.setType(type);
        journalEntry.setCode(code);
        journalEntry.setAmount(amount);
        journalEntry.setOperation(local.getUuid().toString());
        if (amount != null && !amount.equals(BigDecimal.ZERO)) {
            local.getEntries().add(journalEntry);
        }
        return journalEntry;
    }

    @Getter
    @Setter
    public static class Context {
        private final Document document;
        private final List<JournalEntry> entries = new ArrayList<>();
        private UUID uuid;

        public Context(final Document document) {
            this.document = document;
        }

        public UUID getUuid() {
            if (this.uuid == null) {
                this.uuid = UUID.randomUUID();
                return uuid;
            } else {
                final UUID uuid = this.uuid;
                this.uuid = null;
                return uuid;
            }
        }
    }

}
