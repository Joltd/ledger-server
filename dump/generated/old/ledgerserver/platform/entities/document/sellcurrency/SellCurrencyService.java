package old.ledgerserver.platform.entities.document.sellcurrency;

import javax.transaction.Transactional;

import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.stereotype.Service;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import com.evgenltd.ledgerserver.util.ApplicationException;
import java.lang.String;
import com.evgenltd.ledgerserver.common.entity.Account;
import old.ledgerserver.platform.entities.reference.currency.Currency;
import com.evgenltd.ledgerserver.common.entity.ExpenseItem;
import com.evgenltd.ledgerserver.common.entity.IncomeItem;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellCurrencyService {

    private final SellCurrencyRepository sellCurrencyRepository;

    public SellCurrencyService(
        final SellCurrencyRepository sellCurrencyRepository
    ) {
        this.sellCurrencyRepository = sellCurrencyRepository;
    }

    public long count(final LoadConfig loadConfig) {
        return sellCurrencyRepository.count(loadConfig.toSpecification());
    }

    public List<SellCurrencyRow> list(final LoadConfig loadConfig) {
        return sellCurrencyRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).stream()
                .map(this::toRow)
                .toList();
    }

    public List<ReferenceRecord> filter(final String filter) {
        return sellCurrencyRepository.findAll()
                .stream()
                .filter(sellCurrency -> filter == null || filter.isBlank() || sellCurrency.getName().contains(filter))
                .map(this::toReference)
                .collect(Collectors.toList());
    }

    public SellCurrencyRecord byId(final Long id) {
        return sellCurrencyRepository.findById(id)
                .map(this::toRecord)
                .orElseThrow(() -> new ApplicationException("SellCurrency [%s] not found", id));
    }

    @Transactional
    public void update(final SellCurrencyRecord sellCurrencyRecord) {
        final SellCurrency sellCurrency = toEntity(sellCurrencyRecord);
        sellCurrencyRepository.save(sellCurrency);
    }

    @Transactional
    public void delete(final Long id) {
        sellCurrencyRepository.deleteById(id);
    }

    private SellCurrencyRow toRow(final SellCurrency sellCurrency) {
        return new SellCurrencyRow(
            sellCurrency.getId(),
            sellCurrency.getName(),
            sellCurrency.getAccount().getId(),
            sellCurrency.getAccount().getName(),
            sellCurrency.getCurrency().getId(),
            sellCurrency.getCurrency().getName(),
            sellCurrency.getCurrencyRate(),
            sellCurrency.getCurrencyAmount(),
            sellCurrency.getCommission().getId(),
            sellCurrency.getCommission().getName(),
            sellCurrency.getCommissionAmount(),
            sellCurrency.getCurrencySaleIncome().getId(),
            sellCurrency.getCurrencySaleIncome().getName(),
            sellCurrency.getCurrencySaleExpense().getId(),
            sellCurrency.getCurrencySaleExpense().getName()
        );
    }

    private ReferenceRecord toReference(final SellCurrency sellCurrency) {
        return new ReferenceRecord(sellCurrency.getId(), sellCurrency.getName());
    }

    private SellCurrencyRecord toRecord(final SellCurrency sellCurrency) {
        return new SellCurrencyRecord(
            sellCurrency.getId(),
            sellCurrency.getName(),
            new ReferenceRecord(
                sellCurrency.getAccount().getId(),
                sellCurrency.getAccount().getName()
            ),
            new ReferenceRecord(
                sellCurrency.getCurrency().getId(),
                sellCurrency.getCurrency().getName()
            ),
            sellCurrency.getCurrencyRate(),
            sellCurrency.getCurrencyAmount(),
            new ReferenceRecord(
                sellCurrency.getCommission().getId(),
                sellCurrency.getCommission().getName()
            ),
            sellCurrency.getCommissionAmount(),
            new ReferenceRecord(
                sellCurrency.getCurrencySaleIncome().getId(),
                sellCurrency.getCurrencySaleIncome().getName()
            ),
            new ReferenceRecord(
                sellCurrency.getCurrencySaleExpense().getId(),
                sellCurrency.getCurrencySaleExpense().getName()
            )
        );
    }

    private SellCurrency toEntity(final SellCurrencyRecord sellCurrencyRecord) {
        final SellCurrency sellCurrency = new SellCurrency();
        sellCurrency.setName(sellCurrency.getName());
        final Account account = new Account();
        account.setId(sellCurrencyRecord.account().id());
        account.setName(sellCurrencyRecord.account().name());
        sellCurrency.setAccount(account);
        final Currency currency = new Currency();
        currency.setId(sellCurrencyRecord.currency().id());
        currency.setName(sellCurrencyRecord.currency().name());
        sellCurrency.setCurrency(currency);
        sellCurrency.setCurrencyRate(sellCurrency.getCurrencyRate());
        sellCurrency.setCurrencyAmount(sellCurrency.getCurrencyAmount());
        final ExpenseItem commission = new ExpenseItem();
        commission.setId(sellCurrencyRecord.commission().id());
        commission.setName(sellCurrencyRecord.commission().name());
        sellCurrency.setCommission(commission);
        sellCurrency.setCommissionAmount(sellCurrency.getCommissionAmount());
        final IncomeItem currencySaleIncome = new IncomeItem();
        currencySaleIncome.setId(sellCurrencyRecord.currencySaleIncome().id());
        currencySaleIncome.setName(sellCurrencyRecord.currencySaleIncome().name());
        sellCurrency.setCurrencySaleIncome(currencySaleIncome);
        final ExpenseItem currencySaleExpense = new ExpenseItem();
        currencySaleExpense.setId(sellCurrencyRecord.currencySaleExpense().id());
        currencySaleExpense.setName(sellCurrencyRecord.currencySaleExpense().name());
        sellCurrency.setCurrencySaleExpense(currencySaleExpense);
        return sellCurrency;
    }

}