package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.builder.JournalEntryBuilder;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.record.CurrencyAmount;
import com.evgenltd.ledgerserver.service.brocker.TinkoffInvestorCommissionCalculator;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class StockService {

    private final SettingService settingService;
    private final JournalService journalService;
    private final TinkoffInvestorCommissionCalculator commissionCalculator;
    private final CurrencyService currencyService;

    public StockService(
            final SettingService settingService,
            final JournalService journalService,
            final TinkoffInvestorCommissionCalculator commissionCalculator,
            final CurrencyService currencyService
    ) {
        this.settingService = settingService;
        this.journalService = journalService;
        this.commissionCalculator = commissionCalculator;
        this.currencyService = currencyService;
    }

    public void moveToAccount(final LocalDateTime date, final Account from, final Account to, final BigDecimal amount) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, JournalEntry.Codes.C51, amount);
        debit.setAccount(to);

        final JournalEntry credit = JournalEntryBuilder.credit(date, JournalEntry.Codes.C51, amount);
        credit.setAccount(from);

        journalService.persistDoubleJournalEntry(debit, credit);
    }

    public void buyCurrency(
            final LocalDateTime date,
            final Account account,
            final Currency currency,
            final BigDecimal currencyAmount,
            final BigDecimal currencyRate
    ) {
        final Person broker = settingService.broker();
        final ExpenseItem commissionExpense = settingService.brokerCommissionExpenseItem();

        final BigDecimal amount = currencyAmount.multiply(currencyRate);
        final BigDecimal commission = commissionCalculator.calculate(date, amount);

        moveToCurrency(date, amount, account, currency, currencyAmount, currencyRate);
        moveToPerson(date, commission, account, broker);
        personAsExpense(date, commission, broker, commissionExpense);
    }

    public void sellCurrency() {

    }

    public void currencyReassessment(final LocalDateTime date, final Account account, final Currency currency) {
        final CurrencyAmount balance = journalService.currentBalance(
                date.toLocalDate().plusDays(1),
                account,
                currency
        );

        final BigDecimal currencyRate = currencyService.currencyRate(date.toLocalDate(), currency);

        final BigDecimal actualBalance = balance.currencyAmount().multiply(currencyRate);

        final BigDecimal diff = actualBalance.subtract(balance.amount());
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            final IncomeItem reassessment = settingService.currencyReassessmentIncomeItem();
            currencyReassessmentIncrease(date, diff.abs(), account, currency, currencyRate, reassessment);
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            final ExpenseItem reassessment = settingService.currencyReassessmentExpenseItem();
            currencyReassessmentDecrease(date, diff.abs(), account, currency, currencyRate, reassessment);
        }
    }

    public void buyStock(
            final LocalDateTime date,
            final Account account,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count
    ) {
        final BigDecimal amount = price.multiply(new BigDecimal(count));

        final Person broker = settingService.broker();
        final ExpenseItem commissionExpense = settingService.brokerCommissionExpenseItem();

        final BigDecimal commission = commissionCalculator.calculate(date, amount);

        moveToStock(date, amount, account, ticker, price, count);
        moveToPerson(date, commission, account, broker);
        personAsExpense(date, commission, broker, commissionExpense);
    }

    public void sellStock() {

    }

    public void buyStockInCurrency(
            final LocalDateTime date,
            final Account account,
            final Currency currency,
            final BigDecimal currencyRate,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count
    ) {
        currencyReassessment(date, account, currency);
        final BigDecimal currencyAmount = price.multiply(new BigDecimal(count));
        final BigDecimal amount = currencyAmount.multiply(currencyRate);
        moveToStockInCurrency(date, amount, account, currency, currencyAmount, currencyRate, ticker, price, count);
    }

    public void sellStockInCurrency() {

    }

    private void moveToStock(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count
    ) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, JournalEntry.Codes.C58, amount);
        debit.setAccount(account);
        debit.setTickerSymbol(ticker);
        debit.setPrice(price);
        debit.setCount(count);

        final JournalEntry credit = JournalEntryBuilder.credit(date, JournalEntry.Codes.C51, amount);
        credit.setAccount(account);

        journalService.persistDoubleJournalEntry(debit, credit);
    }

    private void moveToStockInCurrency(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final Currency currency,
            final BigDecimal currencyAmount,
            final BigDecimal currencyRate,
            final TickerSymbol ticker,
            final BigDecimal price,
            final Long count
    ) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, JournalEntry.Codes.C58, amount);
        debit.setAccount(account);
        debit.setTickerSymbol(ticker);
        debit.setPrice(price);
        debit.setCount(count);
        debit.setCurrencyAmount(currencyAmount);
        debit.setCurrency(currency);
        debit.setCurrencyRate(currencyRate);

        final JournalEntry credit = JournalEntryBuilder.credit(date, JournalEntry.Codes.C52, amount);
        credit.setAccount(account);
        credit.setCurrencyAmount(currencyAmount);
        credit.setCurrency(currency);
        credit.setCurrencyRate(currencyRate);

        journalService.persistDoubleJournalEntry(debit, credit);
    }

    private void moveToCurrency(
            final LocalDateTime date,
            final BigDecimal amount,
            final Account account,
            final Currency currency,
            final BigDecimal currencyAmount,
            final BigDecimal currencyRate
    ) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, JournalEntry.Codes.C52, amount);
        debit.setAccount(account);
        debit.setCurrency(currency);
        debit.setCurrencyRate(currencyRate);
        debit.setCurrencyAmount(currencyAmount);

        final JournalEntry credit = JournalEntryBuilder.credit(date, JournalEntry.Codes.C51, amount);
        credit.setAccount(account);

        journalService.persistDoubleJournalEntry(debit, credit);
    }

    private void moveToPerson(final LocalDateTime date, final BigDecimal amount, final Account account, final Person person) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, JournalEntry.Codes.C76, amount);
        debit.setPerson(person);

        final JournalEntry credit = JournalEntryBuilder.credit(date, JournalEntry.Codes.C51, amount);
        credit.setAccount(account);

        journalService.persistDoubleJournalEntry(debit, credit);
    }

    private void personAsExpense(final LocalDateTime date, final BigDecimal amount, final Person person, final ExpenseItem expenseItem) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, JournalEntry.Codes.C91_2, amount);
        debit.setExpenseItem(expenseItem);

        final JournalEntry credit = JournalEntryBuilder.credit(date, JournalEntry.Codes.C76, amount);
        credit.setPerson(person);

        journalService.persistDoubleJournalEntry(debit, credit);
    }

    private void currencyReassessmentIncrease(final LocalDateTime date, final BigDecimal amount, final Account account, final Currency currency, final BigDecimal currencyRate, final IncomeItem reassessment) {
        final JournalEntry debit = JournalEntryBuilder.debit(date, JournalEntry.Codes.C52, amount);
        debit.setAccount(account);
        debit.setCurrency(currency);
        debit.setCurrencyRate(currencyRate);

        final JournalEntry credit = JournalEntryBuilder.credit(date, JournalEntry.Codes.C91_1, amount);
        credit.setAccount(account);
        credit.setIncomeItem(reassessment);

        journalService.persistDoubleJournalEntry(debit, credit);
    }

    private void currencyReassessmentDecrease(final LocalDateTime date, final BigDecimal amount, final Account account, final Currency currency, final BigDecimal currencyRate, final ExpenseItem reassessment) {
        final JournalEntry debit = JournalEntryBuilder.credit(date, JournalEntry.Codes.C91_2, amount);
        debit.setAccount(account);
        debit.setExpenseItem(reassessment);

        final JournalEntry credit = JournalEntryBuilder.debit(date, JournalEntry.Codes.C52, amount);
        credit.setAccount(account);
        credit.setCurrency(currency);
        credit.setCurrencyRate(currencyRate);

        journalService.persistDoubleJournalEntry(credit, credit);
    }

}
