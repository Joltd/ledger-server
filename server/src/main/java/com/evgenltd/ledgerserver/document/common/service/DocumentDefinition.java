package com.evgenltd.ledgerserver.document.common.service;

import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.document.common.entity.Document;
import com.evgenltd.ledgerserver.document.common.entity.JournalEntry;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.FieldType;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.MetaField;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.MetaModel;
import com.evgenltd.ledgerserver.reference.entity.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class DocumentDefinition {

    private Document document;
    private ObjectNode definition;
    private final List<JournalEntry> entries = new ArrayList<>();
    private UUID uuid;

    public void setup(final Document document, final ObjectNode definition) {
        this.document = document;
        this.definition = definition;
    }

    public abstract List<MetaField> meta();

    public abstract void persist();

    public MetaModel documentMeta() {
        final List<MetaField> fields = new ArrayList<>();
        fields.add(MetaField.builder().reference("id").type(FieldType.NUMBER).build());
        fields.add(MetaField.builder().reference("date").type(FieldType.DATE).build());
        fields.addAll(meta());
        return new MetaModel(fields);
    }

    protected Document document() {
        return document;
    }

    protected ObjectNode definition() {
        return definition;
    }

    public List<JournalEntry> entities() {
        return entries;
    }

    public void comment(final String comment, final Object... args) {
        document.setComment(String.format(comment, args));
    }

    protected void dt51(
            final BigDecimal amount,
            final Long accountId
    ) {
        final JournalEntry entry = create(JournalEntry.Type.DEBIT, Codes.C51, amount);
        entry.setAccountId(accountId);
    }

    protected void ct51(
            final BigDecimal amount,
            final Long accountId
    ) {
        final JournalEntry entry = create(JournalEntry.Type.CREDIT, Codes.C51, amount);
        entry.setAccountId(accountId);
    }

    protected void dt52(
            final BigDecimal amount,
            final Long accountId,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(JournalEntry.Type.DEBIT, Codes.C52, amount);
        entry.setAccountId(accountId);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    protected void ct52(
            final BigDecimal amount,
            final Long accountId,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(JournalEntry.Type.CREDIT, Codes.C52, amount);
        entry.setAccountId(accountId);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    protected void dt58(
            final BigDecimal amount,
            final Long accountId,
            final Long tickerId,
            final BigDecimal price,
            final Long count,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(JournalEntry.Type.DEBIT, Codes.C58, amount);
        entry.setAccountId(accountId);
        entry.setTickerSymbolId(tickerId);
        entry.setPrice(price);
        entry.setCount(count);
        entry.setCurrency(currency);
        entry.setCurrencyRate(currencyRate);
        entry.setCurrencyAmount(currencyAmount);
    }

    protected void ct58(
            final BigDecimal amount,
            final Long accountId,
            final Long tickerId,
            final BigDecimal price,
            final Long count,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        final JournalEntry entry = create(JournalEntry.Type.CREDIT, Codes.C58, amount);
        entry.setAccountId(accountId);
        entry.setTickerSymbolId(tickerId);
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

    protected void dt76(
            final BigDecimal amount,
            final Long personId
    ) {
        final JournalEntry entry = create(JournalEntry.Type.DEBIT, Codes.C76, amount);
        entry.setPersonId(personId);
    }

    protected void ct76(
            final BigDecimal amount,
            final Long personId
    ) {
        final JournalEntry entry = create(JournalEntry.Type.CREDIT, Codes.C76, amount);
        entry.setPersonId(personId);
    }

    protected void dt80(final BigDecimal amount) {
        create(JournalEntry.Type.DEBIT, Codes.C80, amount);
    }

    protected void ct80(final BigDecimal amount) {
        create(JournalEntry.Type.CREDIT, Codes.C80, amount);
    }

    protected void dt91(
            final BigDecimal amount,
            final Long accountId,
            final Long tickerId,
            final Currency currency,
            final Long expenseItemId
    ) {
        final JournalEntry entry = create(JournalEntry.Type.DEBIT, Codes.C91_2, amount);
        entry.setAccountId(accountId);
        entry.setTickerSymbolId(tickerId);
        entry.setCurrency(currency);
        entry.setExpenseItemId(expenseItemId);
    }

    protected void ct91(
            final BigDecimal amount,
            final Long accountId,
            final Long tickerSymbolId,
            final Currency currency,
            final Long incomeItemId
    ) {
        final JournalEntry entry = create(JournalEntry.Type.CREDIT, Codes.C91_1, amount);
        entry.setAccountId(accountId);
        entry.setTickerSymbolId(tickerSymbolId);
        entry.setCurrency(currency);
        entry.setIncomeItemId(incomeItemId);
    }

    private JournalEntry create(
            final JournalEntry.Type type,
            final String code,
            final BigDecimal amount
    ) {
        final JournalEntry journalEntry = new JournalEntry();
        journalEntry.setDate(document.getDate());
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

    protected BigDecimal asMoney(final String field) {
        if (definition.isEmpty()) {
            return null;
        }
        final String value = definition.get(field).asText();
        return new BigDecimal(value);
    }

    protected Long asLong(final String field) {
        if (definition.isEmpty()) {
            return null;
        }
        return definition.get(field).asLong();
    }

    protected Integer asInteger(final String field) {
        if (definition.isEmpty()) {
            return null;
        }
        return definition.get(field).asInt();
    }

    protected Boolean asBoolean(final String field) {
        if (definition.isEmpty()) {
            return null;
        }
        return definition.get(field).asBoolean();
    }

    protected <T extends Enum<T>> T asEnum(final Class<T> type, final String field) {
        if (definition.isEmpty()) {
            return null;
        }
        return Enum.valueOf(type, definition.get(field).asText());
    }



}
