package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.service.bot.activity.document.SellCurrencyActivity;
import com.evgenltd.ledgerserver.service.bot.activity.document.SellCurrencyStockActivity;
import com.evgenltd.ledgerserver.service.bot.activity.document.SellStockActivity;
import com.evgenltd.ledgerserver.service.brocker.BrokerService;
import com.evgenltd.ledgerserver.util.Utils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final JournalEntryRepository journalEntryRepository;
    private final BrokerService brokerService;
    private final StockRateHistoryService stockRateHistoryService;
    private final SettingService settingService;
    private final BeanFactory beanFactory;

    public StockService(
            final JournalEntryRepository journalEntryRepository,
            final BrokerService brokerService,
            final StockRateHistoryService stockRateHistoryService,
            final SettingService settingService,
            final BeanFactory beanFactory
    ) {
        this.journalEntryRepository = journalEntryRepository;
        this.brokerService = brokerService;
        this.stockRateHistoryService = stockRateHistoryService;
        this.settingService = settingService;
        this.beanFactory = beanFactory;
    }

    public StockPriceRecord collectStockPriceRecord() {
        final List<StockRate> all = stockRateHistoryService.loadAll();
        final List<LocalDate> dates = all.stream()
                .map(StockRate::getDate)
                .distinct()
                .collect(Collectors.toList());
        final List<StockPriceEntryRecord> entries = all.stream()
                .collect(Collectors.groupingBy(
                        StockRate::getTicker,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .map(StockRate::getRate)
                                        .collect(Collectors.toList())
                        )
                ))
                .entrySet()
                .stream()
                .map(entry -> new StockPriceEntryRecord(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return new StockPriceRecord(dates, entries);
    }

    @Transactional
    public PortfolioRecord collectPortfolioAnalysis() {

        final LocalDateTime date = LocalDate.now().plusDays(1L).atStartOfDay().minusSeconds(1L);
        final ExpenseItem commission = settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM);

        final Map<String, Analysis> portfolio = loadPortfolio(date);
        final Analysis total = new Analysis();
        total.setAmount(
                portfolio.values()
                        .stream()
                        .map(Analysis::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        total.setBalance(
                portfolio.values()
                        .stream()
                        .map(Analysis::getBalance)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        calculateTimeWeightedReturn(date, portfolio, total);

        calculateIncome(commission, date, portfolio, total);

        calculateCommission(commission, date, portfolio, total);

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return new PortfolioRecord(
                portfolio.values()
                        .stream()
                        .map(entry -> new PortfolioEntryRecord(
                                Optional.ofNullable(entry.getTicker())
                                        .map(TickerSymbol::getName)
                                        .orElse(
                                                Optional.ofNullable(entry.getCurrency())
                                                        .map(Enum::name)
                                                        .orElse("RUB")
                                        ),
                                entry.getAmount(),
                                entry.getBalance(),
                                entry.getIncome(),
                                entry.getProfitability(),
                                entry.getCommission()
                        ))
                        .collect(Collectors.toList()),
                new PortfolioEntryRecord(
                        null,
                        total.getAmount(),
                        total.getBalance(),
                        total.getIncome(),
                        total.getProfitability(),
                        total.getCommission()
                ),
                rate("USD")
        );

    }

    private Map<String, Analysis> loadPortfolio(final LocalDateTime date) {
        final Map<String, BigDecimal> rateIndex = new HashMap<>();

        return stockJournalEntries(date)
                .stream()
                .collect(Collectors.groupingBy(
                        this::byAccountAndStock,
                        Collectors.reducing(
                                new Analysis(),
                                Analysis::new,
                                Analysis::combine
                        )
                ))
                .values()
                .stream()
                .filter(analysis -> analysis.getTicker() == null || analysis.getCount() > 0)
                .peek(analysis -> {

                    final TickerSymbol ticker = analysis.getTicker();
                    final Currency currency = analysis.getCurrency();

                    if (ticker != null && currency != null) {

                        analysis.setPrice(rateIndex.computeIfAbsent(ticker.getName(), this::rate));
                        analysis.setCurrencyRate(rateIndex.computeIfAbsent(currency.name(), this::rate));

                        final BigDecimal balance = analysis.getPrice()
                                .multiply(new BigDecimal(analysis.getCount()))
                                .multiply(analysis.getCurrencyRate());
                        analysis.setBalance(balance);

                    } else if (ticker != null) {

                        analysis.setPrice(rateIndex.computeIfAbsent(ticker.getName(), this::rate));
                        final BigDecimal balance = analysis.getPrice()
                                .multiply(new BigDecimal(analysis.getCount()));
                        analysis.setBalance(balance);

                    } else if (currency != null) {

                        analysis.setCurrencyRate(rateIndex.computeIfAbsent(currency.name(), this::rate));
                        final BigDecimal balance = analysis.getCurrencyRate()
                                .multiply(analysis.getCurrencyAmount());
                        analysis.setBalance(balance);

                    } else {

                        analysis.setBalance(analysis.getAmount());

                    }

                    sellPortfolioEntry(date, analysis);

                })
                .collect(Collectors.groupingBy(
                        this::byStock,
                        Collectors.reducing(new Analysis(), Analysis::combine)
                ));
    }

    private void sellPortfolioEntry(final LocalDateTime date, final Analysis analysis) {
        final TickerSymbol ticker = analysis.getTicker();
        final Currency currency = analysis.getCurrency();

        if (ticker != null && currency != null) {
            sellCurrencyStock(date, analysis);
        } else if (ticker != null) {
            sellStock(date, analysis);
        } else if (currency != null) {
            sellCurrency(date, analysis);
        }
    }

    private void sellStock(final LocalDateTime date, final Analysis analysis) {
        final Document document = new Document();
        document.setDate(date);
        document.setType(Document.Type.SELLS_STOCK);
        final SellStockActivity sell = beanFactory.getBean(SellStockActivity.class);
        sell.setup(document);

        final Account account = analysis.getAccount();
        final TickerSymbol ticker = analysis.getTicker();
        final Long count = analysis.getCount();
        final BigDecimal price = analysis.getPrice();

        final BigDecimal amount = price.multiply(new BigDecimal(count));
        final BigDecimal commissionAmount = brokerService.calculateCommission(account, date, amount);

        sell.document().set(SellStockActivity.ACCOUNT, account);
        sell.document().set(SellStockActivity.TICKER, ticker);
        sell.document().set(SellStockActivity.COUNT, count);
        sell.document().set(SellStockActivity.PRICE, price);
        sell.document().set(SellStockActivity.COMMISSION_AMOUNT, commissionAmount);

        sell.apply();
    }

    private void sellCurrency(final LocalDateTime date, final Analysis analysis) {
        final Document document = new Document();
        document.setDate(date);
        document.setType(Document.Type.SELL_CURRENCY);
        final SellCurrencyActivity sell = beanFactory.getBean(SellCurrencyActivity.class);
        sell.setup(document);

        final Account account = analysis.getAccount();
        final Currency currency = analysis.getCurrency();
        final BigDecimal currencyRate = analysis.getCurrencyRate();
        final BigDecimal currencyAmount = analysis.getCurrencyAmount();

        final BigDecimal amount = currencyAmount.multiply(currencyRate);
        final BigDecimal commissionAmount = brokerService.calculateCommission(account, date, amount);

        sell.document().set(SellCurrencyActivity.ACCOUNT, account);
        sell.document().set(SellCurrencyActivity.CURRENCY, currency);
        sell.document().set(SellCurrencyActivity.CURRENCY_RATE, currencyRate);
        sell.document().set(SellCurrencyActivity.CURRENCY_AMOUNT, currencyAmount);
        sell.document().set(SellCurrencyActivity.COMMISSION_AMOUNT, commissionAmount);

        sell.apply();
    }

    private void sellCurrencyStock(final LocalDateTime date, final Analysis analysis) {
        final Document document = new Document();
        document.setDate(date);
        document.setType(Document.Type.SELL_CURRENCY_STOCK);
        final SellCurrencyStockActivity sell = beanFactory.getBean(SellCurrencyStockActivity.class);
        sell.setup(document);

        final Account account = analysis.getAccount();
        final TickerSymbol ticker = analysis.getTicker();
        final Long count = analysis.getCount();
        final BigDecimal price = analysis.getPrice();
        final Currency currency = analysis.getCurrency();
        final BigDecimal currencyRate = analysis.getCurrencyRate();

        final BigDecimal currencyAmount = price.multiply(new BigDecimal(count));
        final BigDecimal amount = currencyAmount.multiply(currencyRate);
        final BigDecimal commissionAmount = brokerService.calculateCommission(account, date, amount);

        sell.document().set(SellCurrencyStockActivity.ACCOUNT, account);
        sell.document().set(SellCurrencyStockActivity.TICKER, ticker);
        sell.document().set(SellCurrencyStockActivity.PRICE, price);
        sell.document().set(SellCurrencyStockActivity.COUNT, count);
        sell.document().set(SellCurrencyStockActivity.CURRENCY_RATE, currencyRate);
        sell.document().set(SellCurrencyStockActivity.CURRENCY, currency);
        sell.document().set(SellCurrencyStockActivity.COMMISSION_AMOUNT, commissionAmount);
        sell.document().set(SellCurrencyStockActivity.DIRECT_SELLING, true);

        sell.apply();
    }

    private void calculateTimeWeightedReturn(final LocalDateTime date, final Map<String, Analysis> portfolio, final Analysis total) {
        final List<JournalEntry> entries = stockJournalEntries(date);
        entries.stream()
                .collect(Collectors.groupingBy(
                        this::byStock,
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(
                                        entry -> entry.getDate().toLocalDate(),
                                        Collectors.reducing(BigDecimal.ZERO, JournalEntry::amount, BigDecimal::add)
                                ),
                                this::calculateTimeWeightedReturnForEntry
                        )
                ))
                .forEach((key, twr) -> {
                    final Analysis analysis = portfolio.get(key);
                    if (analysis != null) {
                        analysis.setTimeWeightedAmount(twr);
                    }
                });
        total.setTimeWeightedAmount(
                entries.stream()
                        .collect(Collectors.collectingAndThen(
                                Collectors.groupingBy(
                                        entry -> entry.getDate().toLocalDate(),
                                        Collectors.reducing(BigDecimal.ZERO, JournalEntry::amount, BigDecimal::add)
                                ),
                                this::calculateTimeWeightedReturnForEntry
                        ))
        );
    }

    private BigDecimal calculateTimeWeightedReturnForEntry(final Map<LocalDate, BigDecimal> amountByDate) {
        final List<Map.Entry<LocalDate, BigDecimal>> result = amountByDate.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        LocalDate previous = null;
        long totalDays = 0L;
        BigDecimal cumulativeBalance = BigDecimal.ZERO;
        BigDecimal cumulativeBalanceMultipleDays = BigDecimal.ZERO;

        for (final Map.Entry<LocalDate, BigDecimal> entry : result) {
            if (previous != null) {
                final long days = ChronoUnit.DAYS.between(previous, entry.getKey());
                cumulativeBalanceMultipleDays = cumulativeBalance.multiply(new BigDecimal(days))
                        .add(cumulativeBalanceMultipleDays);
                totalDays += days;
            }
            previous = entry.getKey();
            cumulativeBalance = cumulativeBalance.add(entry.getValue());
        }

        return cumulativeBalanceMultipleDays.divide(new BigDecimal(totalDays), RoundingMode.HALF_DOWN);
    }

    private void calculateIncome(final ExpenseItem commission, final LocalDateTime date, final Map<String, Analysis> portfolio, final Analysis total) {
        final List<JournalEntry> entries = journalEntryRepository.findByDateLessThanEqualAndCodeLike(date, Codes.C91 + "%")
                .stream()
                .filter(journalEntry -> Objects.equals(commission, journalEntry.getExpenseItem()) || journalEntry.getDate().equals(date))
                .collect(Collectors.toList());
        entries.stream()
                .collect(Collectors.groupingBy(
                        this::byStock,
                        Collectors.reducing(BigDecimal.ZERO, JournalEntry::amount, BigDecimal::add)
                )).forEach((key, income) -> {
                    final Analysis analysis = portfolio.get(key);
                    if (analysis != null) {
                        analysis.setIncome(income.negate());
                        analysis.setProfitability(income.negate()
                                .divide(analysis.getTimeWeightedAmount(), RoundingMode.HALF_DOWN)
                                .multiply(new BigDecimal(100)));
                    }
                });
        total.setIncome(
                entries.stream()
                        .map(JournalEntry::amount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .negate()
        );
        total.setProfitability(total.getIncome().divide(total.getTimeWeightedAmount(), RoundingMode.HALF_DOWN).multiply(new BigDecimal(100)));
    }

    private void calculateCommission(final ExpenseItem commission, final LocalDateTime date, final Map<String, Analysis> portfolio, final Analysis total) {
        final List<JournalEntry> entries = journalEntryRepository.findByDateLessThanEqualAndCodeLike(date, Codes.C91_2);
        entries.stream()
                .filter(entry -> Objects.equals(entry.getExpenseItem().getId(), commission.getId()))
                .collect(Collectors.groupingBy(
                        this::byStock,
                        Collectors.reducing(BigDecimal.ZERO, JournalEntry::amount, BigDecimal::add)
                ))
                .forEach((key, commissionAmount) -> {
                    final Analysis analysis = portfolio.get(key);
                    if (analysis != null) {
                        analysis.setCommission(commissionAmount);
                    }
                });
        total.setCommission(
                entries.stream()
                        .filter(entry -> Objects.equals(entry.getExpenseItem().getId(), commission.getId()))
                        .map(JournalEntry::amount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    private List<JournalEntry> stockJournalEntries(final LocalDateTime date) {
        final List<JournalEntry> entries = new ArrayList<>();
        entries.addAll(journalEntryRepository.findByDateLessThanEqualAndCodeLike(date, Codes.C51));
        entries.addAll(journalEntryRepository.findByDateLessThanEqualAndCodeLike(date, Codes.C52));
        entries.addAll(journalEntryRepository.findByDateLessThanEqualAndCodeLike(date, Codes.C58));
        return entries;
    }

    private String byAccountAndStock(final JournalEntry entry) {
        return entry.getAccount().getId()
                + Optional.ofNullable(entry.getTickerSymbol()).map(ticker -> " " + ticker.getId()).orElse("")
                + Optional.ofNullable(entry.getCurrency()).map(currency -> " " + currency.name()).orElse("");
    }

    private String byStock(final Analysis entry) {
        return Optional.ofNullable(entry.getTicker()).map(ticker -> ticker.getId().toString()).orElse("")
                + Optional.ofNullable(entry.getCurrency()).map(currency -> " " + currency.name()).orElse("");
    }

    private String byStock(final JournalEntry entry) {
        return Optional.ofNullable(entry.getTickerSymbol()).map(ticker -> ticker.getId().toString()).orElse("")
                + Optional.ofNullable(entry.getCurrency()).map(currency -> " " + currency.name()).orElse("");
    }

    private BigDecimal rate(final String ticker) {
        return stockRateHistoryService.rate(ticker);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record PortfolioRecord(
            List<PortfolioEntryRecord> entries,
            PortfolioEntryRecord total,
            BigDecimal usd
    ) {}

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record PortfolioEntryRecord(
            String ticker,
            BigDecimal amount,
            BigDecimal balance,
            BigDecimal income,
            BigDecimal profitability,
            BigDecimal commission
    ) {}

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record StockPriceRecord(List<LocalDate> dates, List<StockPriceEntryRecord> entries) {}

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static record StockPriceEntryRecord(String ticker, List<BigDecimal> prices) {}

    public static class Analysis {
        private Account account;
        private TickerSymbol ticker;
        private Long count = 0L;
        private BigDecimal price = BigDecimal.ZERO;
        private Currency currency;
        private BigDecimal currencyAmount = BigDecimal.ZERO;
        private BigDecimal currencyRate = BigDecimal.ZERO;
        private BigDecimal amount = BigDecimal.ZERO; // введено денег
        private BigDecimal balance = BigDecimal.ZERO; // текущий баланс в пересчете с цены
        private BigDecimal timeWeightedAmount = BigDecimal.ZERO; // усредненный объем введенных денег
        private BigDecimal income = BigDecimal.ZERO; // доход, т.е. вся прибыль минус все расходы
        private BigDecimal profitability = BigDecimal.ZERO; // доходность в процентах
        private BigDecimal commission = BigDecimal.ZERO; // сумма потраченная на комиссии

        public Analysis() {}

        public Analysis(final JournalEntry entry) {
            account = entry.getAccount();
            ticker = entry.getTickerSymbol();
            count = entry.count();
            currency = entry.getCurrency();
            currencyAmount = entry.currencyAmount();
            amount = entry.amount();
        }

        public Analysis combine(final Analysis right) {
            final Analysis analysis = new Analysis();
            analysis.account = Utils.ifNull(account, right.account);
            analysis.ticker = Utils.ifNull(ticker, right.ticker);
            analysis.count = count + right.count;
            analysis.price = price.compareTo(BigDecimal.ZERO) != 0 ? price : right.price;
            analysis.currency = Utils.ifNull(currency, right.currency);
            analysis.currencyAmount = currencyAmount.add(right.currencyAmount);
            analysis.currencyRate = currencyRate.compareTo(BigDecimal.ZERO) != 0 ? currencyRate : right.currencyRate;
            analysis.amount = amount.add(right.amount);
            analysis.balance = balance.add(right.balance);
            analysis.timeWeightedAmount = timeWeightedAmount.add(right.timeWeightedAmount);
            analysis.income = income.add(right.income);
            analysis.profitability = profitability.add(right.profitability);
            analysis.commission = commission.add(right.commission);
            return analysis;
        }

        public Account getAccount() {
            return account;
        }

        public void setAccount(final Account account) {
            this.account = account;
        }

        public TickerSymbol getTicker() {
            return ticker;
        }

        public void setTicker(final TickerSymbol ticker) {
            this.ticker = ticker;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(final Long count) {
            this.count = count;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(final BigDecimal price) {
            this.price = price;
        }

        public Currency getCurrency() {
            return currency;
        }

        public void setCurrency(final Currency currency) {
            this.currency = currency;
        }

        public BigDecimal getCurrencyAmount() {
            return currencyAmount;
        }

        public void setCurrencyAmount(final BigDecimal currencyAmount) {
            this.currencyAmount = currencyAmount;
        }

        public BigDecimal getCurrencyRate() {
            return currencyRate;
        }

        public void setCurrencyRate(final BigDecimal currencyRate) {
            this.currencyRate = currencyRate;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(final BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(final BigDecimal balance) {
            this.balance = balance;
        }

        public BigDecimal getTimeWeightedAmount() {
            return timeWeightedAmount;
        }

        public void setTimeWeightedAmount(final BigDecimal timeWeightedAmount) {
            this.timeWeightedAmount = timeWeightedAmount;
        }

        public BigDecimal getIncome() {
            return income;
        }

        public void setIncome(final BigDecimal income) {
            this.income = income;
        }

        public BigDecimal getProfitability() {
            return profitability;
        }

        public void setProfitability(final BigDecimal profitability) {
            this.profitability = profitability;
        }

        public BigDecimal getCommission() {
            return commission;
        }

        public void setCommission(final BigDecimal commission) {
            this.commission = commission;
        }
    }

}
