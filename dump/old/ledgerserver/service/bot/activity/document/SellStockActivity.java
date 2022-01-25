package old.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.common.entity.ExpenseItem;
import com.evgenltd.ledgerserver.common.entity.IncomeItem;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;
import old.ledgerserver.record.StockBalance;
import com.evgenltd.ledgerserver.common.service.JournalService;
import old.ledgerserver.constants.Settings;
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
public class SellStockActivity extends DocumentActivity {

    public static final String ACCOUNT = "account";
    public static final String TICKER = "ticker";
    public static final String PRICE = "price";
    public static final String COUNT = "count";
    public static final String COMMISSION = "commission";
    public static final String COMMISSION_AMOUNT = "commissionAmount";
    public static final String STOCK_SALE_INCOME = "stockSaleIncome";
    public static final String STOCK_SALE_EXPENSE = "stockSaleExpense";

    private final SettingService settingService;
    private final JournalService journalService;

    public SellStockActivity(
            final BeanFactory beanFactory,
            final SettingService settingService,
            final JournalService journalService
    ) {
        super( beanFactory);
        this.settingService = settingService;
        this.journalService = journalService;

        document().accountField(ACCOUNT);
        document().tickerField(TICKER);
        document().moneyField(PRICE);
        document().longField(COUNT);
        document().expenseItemField(COMMISSION);
        document().moneyField(COMMISSION_AMOUNT);
        document().incomeItemField(STOCK_SALE_INCOME);
        document().expenseItemField(STOCK_SALE_EXPENSE);
    }

    @Override
    protected void onDefaults() {
        document().set(PRICE, BigDecimal.ZERO);
        document().set(COMMISSION, settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM));
        document().set(COUNT, 0L);
        document().set(STOCK_SALE_INCOME, settingService.get(Settings.STOCK_SALE_INCOME_ITEM));
        document().set(STOCK_SALE_EXPENSE, settingService.get(Settings.STOCK_SALE_EXPENSE_ITEM));
    }

    @Override
    protected void onSave() {
        final LocalDateTime date = document().get(DATE);
        final Account account = document().get(ACCOUNT);
        final TickerSymbol ticker = document().get(TICKER);
        final BigDecimal price = document().get(PRICE);
        final Long count = document().get(COUNT);
        final ExpenseItem commission = document().get(COMMISSION);
        final BigDecimal commissionAmount = document().get(COMMISSION_AMOUNT);
        final IncomeItem stockSaleIncome = document().get(STOCK_SALE_INCOME);
        final ExpenseItem stockSaleExpense = document().get(STOCK_SALE_EXPENSE);

        final StockBalance balance = journalService.balance(date, account, ticker);
        final BigDecimal averagePrice = balance.balance().divide(new BigDecimal(balance.count()), RoundingMode.HALF_DOWN);
        final BigDecimal withdrawAmount = averagePrice.multiply(new BigDecimal(count));

        document().dt51(date, withdrawAmount, account);
        document().ct58(date, withdrawAmount, account, ticker, averagePrice, count, null, null, null);

        final BigDecimal actualAmount = price.multiply(new BigDecimal(count));
        final BigDecimal diff = actualAmount.subtract(withdrawAmount);
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            document().dt51(date, diff, account);
            document().ct91(date, diff, account, ticker, null, stockSaleIncome);
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            document().dt91(date, diff.abs(), account, ticker, null, stockSaleExpense);
            document().ct51(date, diff.abs(), account);
        }

        document().dt91(date, commissionAmount, account, ticker, null, commission);
        document().ct51(date, commissionAmount, account);

        document().setComment("Sell %s %s", count, ticker.getName());
    }

}
