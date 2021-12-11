package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.builder.ValueInfoBuilder;
import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.document.common.entity.Document;
import com.evgenltd.ledgerserver.document.common.entity.JournalEntry;
import com.evgenltd.ledgerserver.document.common.repository.DocumentRepository;
import com.evgenltd.ledgerserver.document.common.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.reference.entity.Currency;
import com.evgenltd.ledgerserver.record.ValueInfo;
import com.evgenltd.ledgerserver.reference.entity.*;
import com.evgenltd.ledgerserver.util.Utils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DocumentComponent {

    private static final String DATE = "date";
    private static final String COMMENT = "comment";

    private final BeanFactory beanFactory;
    private final DocumentRepository documentRepository;
    private final JournalEntryRepository journalEntryRepository;

    private Document document;
    private final List<JournalEntry> entries = new ArrayList<>();
    private final Map<String, ValueInfo<?>> fields = new HashMap<>();
    private UUID uuid;

    public DocumentComponent(
            final BeanFactory beanFactory,
            final DocumentRepository documentRepository,
            final JournalEntryRepository journalEntryRepository
    ) {
        this.beanFactory = beanFactory;
        this.documentRepository = documentRepository;
        this.journalEntryRepository = journalEntryRepository;
    }

    public void setup(final Document document) {
        this.document = document;
        setContent(document.getContent());
    }

    @Transactional
    public void save() {
        final String content = getContent();
        document.setDate(get(DATE));
        document.setComment(get(COMMENT));
        document.setContent(content);

        documentRepository.save(document);
        journalEntryRepository.deleteByDocumentId(document.getId());
        for (final JournalEntry journalEntry : entries) {
            if (journalEntry.getAmount() != null) {
                journalEntry.setDocument(document);
                journalEntryRepository.save(journalEntry);
            }
        }
    }

    public void setContent(final String content) {
        if (Utils.isBlank(content)) {
            return;
        }
        Stream.of(content.split("\n"))
                .forEach(entry -> {
                    final String[] parts = entry.split(" = ");
                    final String field = parts[0];
                    final String value = parts.length > 1 ? parts[1] : null;
                    getInfo(field).ifPresent(valueInfo -> valueInfo.set(value));
                });
    }

    public String getContent() {
        return fields.entrySet()
                .stream()
                .map(entry -> String.format("%s = %s", entry.getKey(), entry.getValue().asString()))
                .collect(Collectors.joining("\n"));
    }

    public String print() {
        return fields.entrySet()
                .stream()
                .map(entry -> String.format("%s = %s", entry.getKey(), entry.getValue().print()))
                .collect(Collectors.joining("\n"));
    }

    public void setComment(final String comment, final Object... args) {
        set(COMMENT, String.format(comment, args));
    }

    // ##################################################
    // #                                                #
    // #  Field API                                     #
    // #                                                #
    // ##################################################

    public <T> T get(final String field) {
        return get(field, null);
    }

    public <T> T get(final String field, final T defaultValue) {
        return this.<T>getInfo(field).map(ValueInfo::get).orElse(defaultValue);
    }

    public <T> void set(final String field, final T value) {
        this.<T>getInfo(field).ifPresent(info -> info.set(value));
    }

    public void on(final String field, final Runnable callback) {
        getInfo(field).ifPresent(info -> info.on(callback));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<ValueInfo<T>> getInfo(final String field) {
        return fields.entrySet()
                .stream()
                .filter(entry -> Utils.isSimilar(entry.getKey(), field))
                .findFirst()
                .map(entry -> (ValueInfo<T>) entry.getValue());
    }

    // ##################################################
    // #                                                #
    // #  Fields                                        #
    // #                                                #
    // ##################################################

    public void booleanField(final String field) {
        primitiveField(field, Utils::asBoolean, Object::toString, "true,false");
    }

    public void stringField(final String field) {
        primitiveField(field, Optional::of, v -> v, "Any string");
    }

    public void moneyField(final String field) {
        primitiveField(field, Utils::asBigDecimal, BigDecimal::toString, "1234.56");
    }

    public void dateField(final String field) {
        primitiveField(field, Utils::asDateTimeNoThrow, Utils::dateTimeToString, "2020-06-12 12:34:56");
    }

    public void intField(final String field) {
        primitiveField(field, Utils::asIntNoThrow, Object::toString, "1,2,3");
    }

    public void longField(final String field) {
        primitiveField(field, Utils::asLongNoThrow, Objects::toString, "1,2,3");
    }

    public void currencyField(final String field) {
        primitiveField(field, value -> Utils.asEnumNoThrow(value, Currency.class), Enum::name, "USD,RUB");
    }

    public void accountField(final String field) {
//        referenceField(field, beanFactory.getBean(AccountRepository.class));
    }

    public void personField(final String field) {
//        referenceField(field, beanFactory.getBean(PersonRepository.class));
    }

    public void expenseItemField(final String field) {
//        referenceField(field, beanFactory.getBean(ExpenseItemRepository.class));
    }

    public void incomeItemField(final String field) {
//        referenceField(field, beanFactory.getBean(IncomeItemRepository.class));
    }

    public void tickerField(final String field) {
//        referenceField(field, beanFactory.getBean(TickerSymbolRepository.class));
    }

    private  <T> void primitiveField(final String field, final Function<String, Optional<T>> fromString, final Function<T, String> toString, final String example) {
        final ValueInfo<T> valueInfo = ValueInfoBuilder.primitiveValue(fromString, toString, example);
        fields.put(field, valueInfo);
    }

    private <T extends Reference> void referenceField(final String field, final JpaRepository<T, Long> repository) {
        final ValueInfo<T> valueInfo = ValueInfoBuilder.referenceValue(repository);
        fields.put(field, valueInfo);
    }

    // ##################################################
    // #                                                #
    // #  Entries                                       #
    // #                                                #
    // ##################################################

    public void dt51(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.DEBIT, Codes.C51, amount);
        entry.setAccount(account);
    }

    public void ct51(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.CREDIT, Codes.C51, amount);
        entry.setAccount(account);
    }

    public void dt52(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.DEBIT, Codes.C52, amount);
        entry.setAccount(account);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    public void ct52(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.CREDIT, Codes.C52, amount);
        entry.setAccount(account);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    public void dt58(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.DEBIT, Codes.C58, amount);
        entry.setAccount(account);
        entry.setTickerSymbol(ticker);
        entry.setPrice(price);
        entry.setCount(count);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    public void ct58(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.CREDIT, Codes.C58, amount);
        entry.setAccount(account);
        entry.setTickerSymbol(ticker);
        entry.setPrice(price);
        entry.setCount(count);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    public void dt75(final LocalDateTime date, final BigDecimal amount) {
        create(date, JournalEntry.Type.DEBIT, Codes.C75, amount);
    }

    public void ct75(final LocalDateTime date, final BigDecimal amount) {
        create(date, JournalEntry.Type.CREDIT, Codes.C75, amount);
    }

    public void dt76(
            final LocalDateTime date,
            final BigDecimal amount,
            final Person person
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.DEBIT, Codes.C76, amount);
        entry.setPerson(person);
    }

    public void ct76(
            final LocalDateTime date,
            final BigDecimal amount,
            final Person person
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.CREDIT, Codes.C76, amount);
        entry.setPerson(person);
    }

    public void dt80(final LocalDateTime date, final BigDecimal amount) {
        create(date, JournalEntry.Type.DEBIT, Codes.C80, amount);
    }

    public void ct80(final LocalDateTime date, final BigDecimal amount) {
        create(date, JournalEntry.Type.CREDIT, Codes.C80, amount);
    }

    public void dt91(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final TickerSymbol ticker,
            final Currency currency,
            final ExpenseItem expenseItem
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.DEBIT, Codes.C91_2, amount);
        entry.setAccount(account);
        entry.setTickerSymbol(ticker);
        entry.setCurrency(currency);
        entry.setExpenseItem(expenseItem);
    }

    public void ct91(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final TickerSymbol tickerSymbol,
            final Currency currency,
            final IncomeItem incomeItem
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.CREDIT, Codes.C91_1, amount);
        entry.setAccount(account);
        entry.setTickerSymbol(tickerSymbol);
        entry.setCurrency(currency);
        entry.setIncomeItem(incomeItem);
    }

    private JournalEntry create(
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
        if (uuid == null) {
            uuid = UUID.randomUUID();
            journalEntry.setOperation(uuid.toString());
        } else {
            journalEntry.setOperation(uuid.toString());
            uuid = null;
        }
        entries.add(journalEntry);
        return journalEntry;
    }

}
