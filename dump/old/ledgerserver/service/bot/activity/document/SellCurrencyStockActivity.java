package old.ledgerserver.service.bot.activity.document;

import old.ledgerserver.constants.Settings;
import old.ledgerserver.record.StockBalance;
import old.ledgerserver.reference.entity.*;
import com.evgenltd.ledgerserver.common.service.JournalService;
import old.ledgerserver.service.SettingService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SellCurrencyStockActivity extends DocumentActivity {

    public static final String ACCOUNT = "account";
    public static final String TICKER = "ticker";
    public static final String PRICE = "price";
    public static final String COUNT = "count";
    public static final String CURRENCY_RATE = "currencyRate";
    public static final String CURRENCY = "currency";
    public static final String COMMISSION = "commission";
    public static final String COMMISSION_AMOUNT = "commissionAmount";
    public static final String STOCK_SALE_INCOME = "stockSaleIncome";
    public static final String STOCK_SALE_EXPENSE = "stockSaleExpense";
    public static final String DIRECT_SELLING = "directSelling";

    private final SettingService settingService;
    private final JournalService journalService;

    public SellCurrencyStockActivity(
            final BeanFactory beanFactory,
            final SettingService settingService,
            final JournalService journalService
    ) {
        super(beanFactory);
        this.settingService = settingService;
        this.journalService = journalService;

        document().accountField(ACCOUNT);
        document().tickerField(TICKER);
        document().moneyField(PRICE);
        document().longField(COUNT);
        document().moneyField(CURRENCY_RATE);
        document().currencyField(CURRENCY);
        document().expenseItemField(COMMISSION);
        document().moneyField(COMMISSION_AMOUNT);
        document().incomeItemField(STOCK_SALE_INCOME);
        document().expenseItemField(STOCK_SALE_EXPENSE);
        document().booleanField(DIRECT_SELLING);
    }

    @Override
    protected void onDefaults() {
        document().set(CURRENCY, Currency.USD);
        document().set(COMMISSION, settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM));
        document().set(COUNT, 0L);
        document().set(STOCK_SALE_INCOME, settingService.get(Settings.STOCK_SALE_INCOME_ITEM));
        document().set(STOCK_SALE_EXPENSE, settingService.get(Settings.STOCK_SALE_EXPENSE_ITEM));
        document().set(DIRECT_SELLING, false);
    }

    @Override
    protected void onSave() {
        final LocalDateTime date = document().get(DATE);
        final Account account = document().get(ACCOUNT);
        final TickerSymbol ticker = document().get(TICKER);
        final BigDecimal price = document().get(PRICE);
        final Long count = document().get(COUNT);
        final BigDecimal currencyRate = document().get(CURRENCY_RATE);
        final Currency currency = document().get(CURRENCY);
        final ExpenseItem commission = document().get(COMMISSION);
        final BigDecimal commissionAmount = document().get(COMMISSION_AMOUNT);
        final IncomeItem stockSaleIncome = document().get(STOCK_SALE_INCOME);
        final ExpenseItem stockSaleExpense = document().get(STOCK_SALE_EXPENSE);
        final Boolean directSelling = document().get(DIRECT_SELLING);

        final StockBalance balance = journalService.balance(date, account, ticker);
        final BigDecimal averagePrice = balance.currencyBalance().divide(new BigDecimal(balance.count()), RoundingMode.HALF_DOWN);
        final BigDecimal currencyWithdrawAmount = averagePrice.multiply(new BigDecimal(count));
        final BigDecimal averageCurrencyRate = balance.balance().divide(balance.currencyBalance(), RoundingMode.HALF_DOWN);
        final BigDecimal withdrawAmount = averageCurrencyRate.multiply(currencyWithdrawAmount);

        if (!directSelling) {
            document().dt52(date, withdrawAmount, account, currency, averageCurrencyRate, currencyWithdrawAmount);
            document().ct58(date, withdrawAmount, account, ticker, price, count, currency, averageCurrencyRate, currencyWithdrawAmount);

            final BigDecimal actualCurrencyAmount = price.multiply(new BigDecimal(count));
            final BigDecimal currencyDiff = actualCurrencyAmount.subtract(currencyWithdrawAmount);
            final BigDecimal diff = currencyDiff.multiply(averageCurrencyRate);
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                document().dt52(date, diff, account, currency, averageCurrencyRate, currencyDiff);
                document().ct91(date, diff, account, ticker, currency, stockSaleIncome);
            } else if (currencyDiff.compareTo(BigDecimal.ZERO) < 0) {
                document().dt91(date, diff.abs(), account, ticker, currency, stockSaleExpense);
                document().ct52(date, diff.abs(), account, currency, averageCurrencyRate, currencyDiff.abs());
            }

            if (ticker.getWithoutCommission() != null && !ticker.getWithoutCommission()) {
                // todo commission for ticker selling
            }
        } else {
            document().dt51(date, withdrawAmount, account);
            document().ct58(date, withdrawAmount, account, ticker, price, count, currency, averageCurrencyRate, currencyWithdrawAmount);

            final BigDecimal actualCurrencyAmount = price.multiply(new BigDecimal(count));
            final BigDecimal actualAmount = actualCurrencyAmount.multiply(currencyRate);
            final BigDecimal diff = actualAmount.subtract(withdrawAmount);
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                document().dt51(date, diff, account);
                document().ct91(date, diff, account, ticker, currency, stockSaleIncome);
            } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                document().dt91(date, diff.abs(), account, ticker, currency, stockSaleExpense);
                document().ct51(date, diff.abs(), account);
            }

            if (ticker.getWithoutCommission() != null && !ticker.getWithoutCommission()) {
                // todo commission for ticker selling
            }

            document().dt91(date, commissionAmount, account, ticker, currency, commission);
            document().ct51(date, commissionAmount, account);
        }

        document().setComment("Sell %s %s", count, ticker.getName());
    }

}
