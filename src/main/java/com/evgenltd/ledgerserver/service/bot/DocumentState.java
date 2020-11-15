package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.record.ValueInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public class DocumentState {
    
    private static final ThreadLocal<DocumentComponent> context = new ThreadLocal<>();

    public static void set(final DocumentComponent documentComponent) {
        context.set(documentComponent);
    }
    
    private static DocumentComponent documentComponent() {
        return context.get();
    }
    
    public static void reset() {
        context.remove();
    }

    public static void setContent(final String content) {
        documentComponent().setContent(content);
    }

    public static String getContent() {
        return documentComponent().getContent();
    }

    public static <T> T get(final String field) {
        return documentComponent().get(field);
    }

    public static <T> T get(final String field, final T defaultValue) {
        return documentComponent().get(field, defaultValue);
    }

    public static <T> void set(final String field, final T value) {
        documentComponent().set(field, value);
    }

    public static void on(final String field, final Runnable callback) {
        documentComponent().on(field, callback);
    }

    public static <T> Optional<ValueInfo<T>> getInfo(final String field) {
        return documentComponent().getInfo(field);
    }

    public static void moneyField(final String field) {
        documentComponent().moneyField(field);
    }

    public static void dateField(final String field) {
        documentComponent().dateField(field);
    }

    public static void intField(final String field) {
        documentComponent().intField(field);
    }

    public static void longField(final String field) {
        documentComponent().longField(field);
    }

    public static void currencyField(final String field) {
        documentComponent().currencyField(field);
    }

    public static void accountField(final String field) {
        documentComponent().accountField(field);
    }

    public static void personField(final String field) {
        documentComponent().personField(field);
    }

    public static void expenseItemField(final String field) {
        documentComponent().expenseItemField(field);
    }

    public static void incomeItemField(final String field) {
        documentComponent().incomeItemField(field);
    }

    public static void tickerField(final String field) {
        documentComponent().tickerField(field);
    }

    public static void dt51(final String amount, final String account) {
        documentComponent().dt51(amount, account);
    }

    public static void dt51(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account
    ) {
        documentComponent().dt51(date, amount, account);
    }

    public static void ct51(final String amount, final String account) {
        documentComponent().ct51(amount, account);
    }

    public static void ct51(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account
    ) {
        documentComponent().ct51(date, amount, account);
    }

    public static void dt52(
            final String amount,
            final String account,
            final String currency,
            final String currencyRate,
            final String currencyAmount
    ) {
        documentComponent().dt52(amount, account, currency, currencyRate, currencyAmount);
    }

    public static void dt52(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        documentComponent().dt52(date, amount, account, currency, currencyRate, currencyAmount);
    }

    public static void ct52(
            final String amount,
            final String account,
            final String currency,
            final String currencyRate,
            final String currencyAmount
    ) {
        documentComponent().ct52(amount, account, currency, currencyRate, currencyAmount);
    }

    public static void ct52(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final Currency currency,
            final BigDecimal currencyRate,
            final BigDecimal currencyAmount
    ) {
        documentComponent().ct52(date, amount, account, currency, currencyRate, currencyAmount);
    }

    public static void dt58(
            final String amount,
            final String account,
            final String ticker,
            final String price,
            final String count
    ) {
        dt58(amount, account, ticker, price, count, "", "", "");
    }

    public static void dt58(
            final String amount,
            final String account,
            final String ticker,
            final String price,
            final String count,
            final String currency,
            final String currencyRate,
            final String currencyAmount
    ) {
        documentComponent().dt58(amount, account, ticker, price, count, currency, currencyRate, currencyAmount);
    }

    public static void dt58(
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
        documentComponent().dt58(date, amount, account, ticker, price, count, currency, currencyRate, currencyAmount);
    }

    public static void ct58(
            final String amount,
            final String account,
            final String ticker,
            final String price,
            final String count
    ) {
        ct58(amount, account, ticker, price, count);
    }

    public static void ct58(
            final String amount,
            final String account,
            final String ticker,
            final String price,
            final String count,
            final String currency,
            final String currencyRate,
            final String currencyAmount
    ) {
        documentComponent().ct58(amount, account, ticker, price, count, currency, currencyRate, currencyAmount);
    }

    public static void ct58(
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
        documentComponent().ct58(date, amount, account, ticker, price, count, currency, currencyRate, currencyAmount);
    }

    public static void dt75(final String amount) {
        documentComponent().dt75(amount);
    }

    public static void dt75(final LocalDateTime date, final BigDecimal amount) {
        documentComponent().dt75(date, amount);
    }

    public static void ct75(final String amount) {
        documentComponent().ct75(amount);
    }

    public static void ct75(final LocalDateTime date, final BigDecimal amount) {
        documentComponent().ct75(date, amount);
    }

    public static void dt76(final String amount, final String person) {
        documentComponent().dt76(amount, person);
    }

    public static void dt76(
            final LocalDateTime date,
            final BigDecimal amount,
            final Person person
    ) {
        documentComponent().dt76(date, amount, person);
    }

    public static void ct76(final String amount, final String person) {
        documentComponent().ct76(amount, person);
    }

    public static void ct76(
            final LocalDateTime date,
            final BigDecimal amount,
            final Person person
    ) {
        documentComponent().ct76(date, amount, person);
    }

    public static void dt80(final String amount) {
        documentComponent().dt80(amount);
    }

    public static void dt80(final LocalDateTime date, final BigDecimal amount) {
        documentComponent().dt80(date, amount);
    }

    public static void ct80(final String amount) {
        documentComponent().ct80(amount);
    }

    public static void ct80(final LocalDateTime date, final BigDecimal amount) {
        documentComponent().ct80(date, amount);
    }

    public static void dt91(final String amount, final String expenseItem) {
        documentComponent().dt91(amount, expenseItem);
    }

    public static void dt91(
            final LocalDateTime date,
            final BigDecimal amount,
            final ExpenseItem expenseItem
    ) {
        documentComponent().dt91(date, amount, expenseItem);
    }

    public static void ct91(final String amount, final String incomeItem) {
        documentComponent().ct91(amount, incomeItem);
    }

    public static void ct91(
            final LocalDateTime date,
            final BigDecimal amount,
            final IncomeItem incomeItem
    ) {
        documentComponent().ct91(date, amount, incomeItem);
    }

    public static void reassessment52(final String account, final String currency, final String newCurrencyRate) {
        documentComponent().reassessment52(account, currency, newCurrencyRate);
    }

    public static void reassessment58(final String account, final String ticker, final String newPrice) {
        documentComponent().reassessment58(account, ticker, newPrice);
    }

    public static void reassessment58(
            final String account,
            final String ticker,
            final String currency,
            final String newCurrencyRate,
            final String newPrice
    ) {
        documentComponent().reassessment58(account, ticker, currency, newCurrencyRate, newPrice);
    }
}
