package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.builder.ValueInfoBuilder;
import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.record.StockBalance;
import com.evgenltd.ledgerserver.record.ValueInfo;
import com.evgenltd.ledgerserver.repository.*;
import com.evgenltd.ledgerserver.service.SettingService;
import com.evgenltd.ledgerserver.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DocumentComponent {

    private static final String DATE = "date";

    private final BeanFactory beanFactory;
    private final DocumentRepository documentRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final SettingService settingService;
    
    private Document document;
    private final List<JournalEntry> entries = new ArrayList<>();
    private final Map<String, ValueInfo<?>> fields = new HashMap<>();
    private UUID uuid;

    public DocumentComponent(
            final BeanFactory beanFactory,
            final DocumentRepository documentRepository,
            final JournalEntryRepository journalEntryRepository,
            final SettingService settingService
    ) {
        this.beanFactory = beanFactory;
        this.documentRepository = documentRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.settingService = settingService;
    }

    public void setup(final Document document) {
        this.document = document;
        setContent(document.getContent());
    }

    @Transactional
    public void save() {
        final String content = getContent();
        document.setDate(get(DATE));
        document.setContent(content);

        documentRepository.save(document);
        journalEntryRepository.deleteByDocumentId(document.getId());
        for (final JournalEntry journalEntry : entries) {
            journalEntry.setDocument(document);
            journalEntryRepository.save(journalEntry);
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
        primitiveField(field, value -> Utils.asEnumNoThrow(value, com.evgenltd.ledgerserver.entity.Currency.class), Enum::name, "USD,RUB");
    }

    public void accountField(final String field) {
        referenceField(field, beanFactory.getBean(AccountRepository.class));
    }

    public void personField(final String field) {
        referenceField(field, beanFactory.getBean(PersonRepository.class));
    }

    public void expenseItemField(final String field) {
        referenceField(field, beanFactory.getBean(ExpenseItemRepository.class));
    }

    public void incomeItemField(final String field) {
        referenceField(field, beanFactory.getBean(IncomeItemRepository.class));
    }

    public void tickerField(final String field) {
        referenceField(field, beanFactory.getBean(TickerSymbolRepository.class));
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

    public void dt51(final String amount, final String account) {
        dt51(get(DATE), get(amount), get(account));
    }

    public void dt51(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.DEBIT, Codes.C51, amount);
        entry.setAccount(account);
    }
    public void ct51(final String amount, final String account) {
        ct51(get(DATE), get(amount), get(account));
    }

    public void ct51(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.CREDIT, Codes.C51, amount);
        entry.setAccount(account);
    }

    public void dt52(final String amount, final String account, final String currency, final String currencyRate, final String currencyAmount) {
        dt52(get(DATE), get(amount), get(account), get(currency), get(currencyRate), get(currencyAmount));
    }

    public void dt52(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final com.evgenltd.ledgerserver.entity.Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.DEBIT, Codes.C52, amount);
        entry.setAccount(account);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    public void ct52(final String amount, final String account, final String currency, final String currencyRate, final String currencyAmount) {
        ct52(get(DATE), get(amount), get(account), get(currency), get(currencyRate), get(currencyAmount));
    }

    public void ct52(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final com.evgenltd.ledgerserver.entity.Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.CREDIT, Codes.C52, amount);
        entry.setAccount(account);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    public void dt58(final String amount, final String account, final String ticker, final String price, final String count, final String currency, final String currencyRate, final String currencyAmount) {
        dt58(get(DATE), get(amount), get(account), get(ticker), get(price), get(count), get(currency), get(currencyRate), get(currencyAmount));
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

    public void ct58(final String amount, final String account, final String ticker, final String price, final String count, final String currency, final String currencyRate, final String currencyAmount) {
        ct58(get(DATE), get(amount), get(account), get(ticker), get(price), get(count), get(currency), get(currencyRate), get(currencyAmount));
    }

    public void ct58(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count,
            final com.evgenltd.ledgerserver.entity.Currency currency,
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

    public void dt75(final String amount) {
        dt75(get(DATE), get(amount));
    }

    public void dt75(final LocalDateTime date, final BigDecimal amount) {
        create(date, JournalEntry.Type.DEBIT, Codes.C75, amount);
    }

    public void ct75(final String amount) {
        ct75(get(DATE), get(amount));
    }

    public void ct75(final LocalDateTime date, final BigDecimal amount) {
        create(date, JournalEntry.Type.CREDIT, Codes.C75, amount);
    }

    public void dt76(final String amount, final String person) {
        dt76(get(DATE), get(amount), get(person));
    }

    public void dt76(
            final LocalDateTime date,
            final BigDecimal amount,
            final Person person
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.DEBIT, Codes.C76, amount);
        entry.setPerson(person);
    }
    public void ct76(final String amount, final String person) {
        ct76(get(DATE), get(amount), get(person));
    }

    public void ct76(
            final LocalDateTime date,
            final BigDecimal amount,
            final Person person
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.CREDIT, Codes.C76, amount);
        entry.setPerson(person);
    }

    public void dt80(final String amount) {
        dt80(get(DATE), get(amount));
    }

    public void dt80(final LocalDateTime date, final BigDecimal amount) {
        create(date, JournalEntry.Type.DEBIT, Codes.C80, amount);
    }

    public void ct80(final String amount) {
        ct80(get(DATE), get(amount));
    }

    public void ct80(final LocalDateTime date, final BigDecimal amount) {
        create(date, JournalEntry.Type.CREDIT, Codes.C80, amount);
    }

    public void dt91(final String amount, final String expenseItem) {
        dt91(get(DATE), get(amount), get(expenseItem));
    }

    public void dt91(
            final LocalDateTime date,
            final BigDecimal amount,
            final ExpenseItem expenseItem
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.DEBIT, Codes.C91_2, amount);
        entry.setExpenseItem(expenseItem);
    }

    public void ct91(final String amount, final String incomeItem) {
        ct91(get(DATE), get(amount), get(incomeItem));
    }

    public void ct91(
            final LocalDateTime date,
            final BigDecimal amount,
            final IncomeItem incomeItem
    ) {
        final JournalEntry entry = create(date, JournalEntry.Type.CREDIT, Codes.C91_1, amount);
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

    public void reassessment52(final String account, final String currency, final String newCurrencyRate) {
        final StockBalance balance = balance(get(DATE), get(account), (Currency) get(currency));

        final BigDecimal actualBalance = balance.currencyBalance().multiply(get(newCurrencyRate));

        final BigDecimal diff = actualBalance.subtract(balance.balance());
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            final IncomeItem reassessment = settingService.get(Settings.CURRENCY_REASSESSMENT_INCOME_ITEM);
            dt52(get(DATE), diff, get(account), get(currency), get(newCurrencyRate), null);
            ct91(get(DATE), diff, reassessment);
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            final ExpenseItem reassessment = settingService.get(Settings.CURRENCY_REASSESSMENT_EXPENSE_ITEM);
            dt91(get(DATE), diff.abs(), reassessment);
            ct52(get(DATE), diff.abs(), get(account), get(currency), get(newCurrencyRate), null);
        }
    }

    public void reassessment58(final String account, final String ticker, final String newPrice) {
        final StockBalance balance = balance(get(DATE), get(account), (TickerSymbol) get(ticker));

        final BigDecimal newBalance = balance.currencyBalance().multiply(get(newPrice));

        final BigDecimal diff = newBalance.subtract(balance.balance());
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            final IncomeItem reassessment = settingService.get(Settings.STOCK_REASSESSMENT_INCOME_ITEM);
            dt58(get(DATE), diff, get(account), get(ticker), get(newPrice), null, null, null, null);
            ct91(get(DATE), diff, reassessment);
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            final ExpenseItem reassessment = settingService.get(Settings.STOCK_REASSESSMENT_EXPENSE_ITEM);
            dt91(get(DATE), diff.negate(), reassessment);
            ct58(get(DATE), diff.negate(), get(account), get(ticker), get(newPrice), null, null, null, null);
        }
    }

    public void reassessment58(final String account, final String ticker, final String currency, final String newCurrencyRate, final String newPrice) {
        final StockBalance balance = balance(get(DATE), get(account), (TickerSymbol) get(ticker));

        final BigDecimal newCurrencyAmount = new BigDecimal(balance.count()).multiply(get(newPrice));
        final BigDecimal newBalance = balance.currencyBalance().multiply(get(newCurrencyRate));

        final BigDecimal diff = newBalance.subtract(balance.balance());
        final BigDecimal currencyDiff = newCurrencyAmount.subtract(balance.currencyBalance());
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            final IncomeItem reassessment = settingService.get(Settings.STOCK_REASSESSMENT_INCOME_ITEM);
            dt58(get(DATE), diff, get(account), get(ticker), get(newPrice), null, get(currency), get(newCurrencyRate), currencyDiff);
            ct91(get(DATE), diff, reassessment);
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            final ExpenseItem reassessment = settingService.get(Settings.STOCK_REASSESSMENT_EXPENSE_ITEM);
            dt91(get(DATE), diff.negate(), reassessment);
            ct58(get(DATE), diff.negate(), get(account), get(ticker), get(newPrice), null, get(currency), get(newCurrencyRate), currencyDiff.negate());
        }
    }

    // ##################################################
    // #                                                #
    // #  Support methods                               #
    // #                                                #
    // ##################################################

    private StockBalance balance(final LocalDateTime date, final Account account, final TickerSymbol tickerSymbol) {
        final List<JournalEntry> result = journalEntryRepository.findByDateLessThanAndCodeAndAccountAndTickerSymbol(
                date,
                Codes.C58,
                account,
                tickerSymbol
        );

        return calculateStockBalance(result);
    }

    private StockBalance balance(final LocalDateTime date, final Account account, final Currency currency) {
        final List<JournalEntry> result = journalEntryRepository.findByDateLessThanAndCodeAndAccountAndCurrency(
                date,
                Codes.C52,
                account,
                currency
        );

        return calculateStockBalance(result);
    }

    @NotNull
    private StockBalance calculateStockBalance(final List<JournalEntry> result) {
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal currencyBalance = BigDecimal.ZERO;
        long count = 0L;
        for (JournalEntry entry : result) {
            switch (entry.getType()) {
                case DEBIT -> {
                    balance = balance.add(entry.getAmount());
                    final BigDecimal currencyAmount = entry.getCurrencyAmount() != null
                            ? entry.getCurrencyAmount()
                            : BigDecimal.ZERO;
                    currencyBalance = currencyBalance.add(currencyAmount);
                    count += entry.getCount() != null ? entry.getCount() : 0L;
                }
                case CREDIT -> {
                    balance = balance.subtract(entry.getAmount());
                    final BigDecimal currencyAmount = entry.getCurrencyAmount() != null
                            ? entry.getCurrencyAmount()
                            : BigDecimal.ZERO;
                    currencyBalance = currencyBalance.subtract(currencyAmount);
                    count -= entry.getCount() != null ? entry.getCount() : 0L;
                }
            }
        }

        return new StockBalance(balance, currencyBalance, count);
    }
    
}
