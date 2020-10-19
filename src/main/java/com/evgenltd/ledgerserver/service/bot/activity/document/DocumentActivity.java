package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.ApplicationException;
import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.builder.DocumentBuilder;
import com.evgenltd.ledgerserver.builder.ValueInfoBuilder;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.record.ValueInfo;
import com.evgenltd.ledgerserver.repository.*;
import com.evgenltd.ledgerserver.service.JournalService;
import com.evgenltd.ledgerserver.service.bot.BotActivity;
import com.evgenltd.ledgerserver.service.bot.BotService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DocumentActivity extends BotActivity {

    private final JournalService journalService;
    private final JournalEntryRepository journalEntryRepository;
    private final DocumentRepository documentRepository;
    private final BeanFactory beanFactory;

    private Document document;
    private final List<JournalEntry> journalEntries = new ArrayList<>();

    private final Map<String, ValueInfo<?>> fields = new HashMap<>();


    public DocumentActivity(
            final BotService botService,
            final BeanFactory beanFactory
    ) {
        super(botService);
        this.journalService = beanFactory.getBean(JournalService.class);
        this.journalEntryRepository = beanFactory.getBean(JournalEntryRepository.class);
        this.documentRepository = beanFactory.getBean(DocumentRepository.class);
        this.beanFactory = beanFactory;

        dateField("date");
    }

    public void setup(final Document.Type type, final Long documentId) {
        document = Optional.ofNullable(documentId)
                .flatMap(documentRepository::findById)
                .orElse(DocumentBuilder.buildDocument(type));

        if (document.getId() != null) {
            journalEntries.addAll(journalEntryRepository.findByDocumentId(documentId));
            onSetup();
        } else {
            defaults();
        }
    }

    private void defaults() {
        set("date", document.getDate());
        onDefaults();
    }

    @Override
    protected void onMessageReceived(final String message) {
        final FirstWord wordAndMessage = splitFirstWord(message);
        final String command = wordAndMessage.word();

        final Optional<ValueInfo<Object>> valueInfo = getInfo(command);
        if (valueInfo.isPresent()) {
            if (wordAndMessage.message().isBlank()) {
                sendMessage(valueInfo.get().example());
            } else {
                valueInfo.get().set(wordAndMessage.message());
                hello();
            }
            return;
        }

        if (Utils.isSimilar(command, "save", "apply")) {
            save();
            activityBack();
        } else if (Utils.isSimilar(command, "discard", "cancel", "close", "back")) {
            activityBack();
        }
    }

    @Override
    protected void hello() {
        final String all = fields.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> String.format("%s = %s", entry.getKey(), entry.getValue().print()))
                .collect(Collectors.joining("\n"));
        sendMessage(all);
    }

    //

    protected abstract void onSetup();

    protected void onDefaults() {}

    protected abstract void onSave();

    //


    protected Document getDocument() {
        return document;
    }

    protected List<JournalEntry> getJournalEntries() {
        return journalEntries;
    }

    protected JournalEntry byDebitCode(final String code) {
        return journalEntries.stream()
                .filter(journalEntry -> journalEntry.getType().equals(JournalEntry.Type.DEBIT) && journalEntry.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("Journal entry Dt %s not found", code));
    }

    protected JournalEntry byCreditCode(final String code) {
        return journalEntries.stream()
                .filter(journalEntry -> journalEntry.getType().equals(JournalEntry.Type.DEBIT) && journalEntry.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("Journal entry Ct %s not found", code));
    }

    protected  <T> T get(final String field) {
        return this.<T>getInfo(field).map(ValueInfo::get).orElse(null);
    }

    protected  <T> void set(final String field, final T value) {
        this.<T>getInfo(field).ifPresent(info -> info.set(value));
    }

    protected void on(final String field, final Runnable callback) {
        getInfo(field).ifPresent(info -> info.on(callback));
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<ValueInfo<T>> getInfo(final String field) {
        return fields.entrySet()
                .stream()
                .filter(entry -> Utils.isSimilar(entry.getKey(), field))
                .findFirst()
                .map(entry -> (ValueInfo<T>) entry.getValue());
    }

    private void save() {
        document.setDate(get("date"));
        journalEntries.clear();
        onSave();
        journalService.persistDocument(document, journalEntries);
    }

    //

    protected void moneyField(final String field) {
        primitiveField(field, Utils::asBigDecimal, "1234.56");
    }

    protected void dateField(final String field) {
        primitiveField(field, Utils::asDateTimeNoThrow, "2020-06-12 12:34:56");
    }

    protected void intField(final String field) {
        primitiveField(field, Utils::asIntNoThrow, "1,2,3");
    }

    protected void longField(final String field) {
        primitiveField(field, Utils::asLongNoThrow, "1,2,3");
    }

    protected void currencyField(final String field) {
        primitiveField(field, value -> Utils.asEnumNoThrow(value, Currency.class), "USD,RUB");
    }

    protected void accountField(final String field) {
        referenceField(field, beanFactory.getBean(AccountRepository.class));
    }

    protected void personField(final String field) {
        referenceField(field, beanFactory.getBean(PersonRepository.class));
    }

    protected void expenseItemField(final String field) {
        referenceField(field, beanFactory.getBean(ExpenseItemRepository.class));
    }

    protected void incomeItemField(final String field) {
        referenceField(field, beanFactory.getBean(IncomeItemRepository.class));
    }

    protected void tickerField(final String field) {
        referenceField(field, beanFactory.getBean(TickerSymbolRepository.class));
    }

    private <T> void primitiveField(final String field, final Function<String, Optional<T>> converter, final String example) {
        final ValueInfo<T> valueInfo = ValueInfoBuilder.primitiveValue(converter, example);
        fields.put(field, valueInfo);
    }

    private <T extends Reference> void referenceField(final String field, final JpaRepository<T, Long> repository) {
        final ValueInfo<T> valueInfo = ValueInfoBuilder.referenceValue(repository);
        fields.put(field, valueInfo);
    }

}
