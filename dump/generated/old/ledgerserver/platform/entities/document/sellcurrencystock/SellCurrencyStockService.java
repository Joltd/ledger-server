package old.ledgerserver.platform.entities.document.sellcurrencystock;

import javax.transaction.Transactional;

import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.stereotype.Service;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import com.evgenltd.ledgerserver.util.ApplicationException;
import java.lang.String;
import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;

import java.lang.Long;

import old.ledgerserver.platform.entities.reference.currency.Currency;
import com.evgenltd.ledgerserver.common.entity.ExpenseItem;
import com.evgenltd.ledgerserver.common.entity.IncomeItem;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellCurrencyStockService {

    private final SellCurrencyStockRepository sellCurrencyStockRepository;

    public SellCurrencyStockService(
        final SellCurrencyStockRepository sellCurrencyStockRepository
    ) {
        this.sellCurrencyStockRepository = sellCurrencyStockRepository;
    }

    public long count(final LoadConfig loadConfig) {
        return sellCurrencyStockRepository.count(loadConfig.toSpecification());
    }

    public List<SellCurrencyStockRow> list(final LoadConfig loadConfig) {
        return sellCurrencyStockRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).stream()
                .map(this::toRow)
                .toList();
    }

    public List<ReferenceRecord> filter(final String filter) {
        return sellCurrencyStockRepository.findAll()
                .stream()
                .filter(sellCurrencyStock -> filter == null || filter.isBlank() || sellCurrencyStock.getName().contains(filter))
                .map(this::toReference)
                .collect(Collectors.toList());
    }

    public SellCurrencyStockRecord byId(final Long id) {
        return sellCurrencyStockRepository.findById(id)
                .map(this::toRecord)
                .orElseThrow(() -> new ApplicationException("SellCurrencyStock [%s] not found", id));
    }

    @Transactional
    public void update(final SellCurrencyStockRecord sellCurrencyStockRecord) {
        final SellCurrencyStock sellCurrencyStock = toEntity(sellCurrencyStockRecord);
        sellCurrencyStockRepository.save(sellCurrencyStock);
    }

    @Transactional
    public void delete(final Long id) {
        sellCurrencyStockRepository.deleteById(id);
    }

    private SellCurrencyStockRow toRow(final SellCurrencyStock sellCurrencyStock) {
        return new SellCurrencyStockRow(
            sellCurrencyStock.getId(),
            sellCurrencyStock.getName(),
            sellCurrencyStock.getAccount().getId(),
            sellCurrencyStock.getAccount().getName(),
            sellCurrencyStock.getTicker().getId(),
            sellCurrencyStock.getTicker().getName(),
            sellCurrencyStock.getPrice(),
            sellCurrencyStock.getCount(),
            sellCurrencyStock.getCurrencyRate(),
            sellCurrencyStock.getCurrency().getId(),
            sellCurrencyStock.getCurrency().getName(),
            sellCurrencyStock.getCommission().getId(),
            sellCurrencyStock.getCommission().getName(),
            sellCurrencyStock.getCommissionAmount(),
            sellCurrencyStock.getStockSaleIncome().getId(),
            sellCurrencyStock.getStockSaleIncome().getName(),
            sellCurrencyStock.getStockSaleExpense().getId(),
            sellCurrencyStock.getStockSaleExpense().getName(),
            sellCurrencyStock.getDirectSelling()
        );
    }

    private ReferenceRecord toReference(final SellCurrencyStock sellCurrencyStock) {
        return new ReferenceRecord(sellCurrencyStock.getId(), sellCurrencyStock.getName());
    }

    private SellCurrencyStockRecord toRecord(final SellCurrencyStock sellCurrencyStock) {
        return new SellCurrencyStockRecord(
            sellCurrencyStock.getId(),
            sellCurrencyStock.getName(),
            new ReferenceRecord(
                sellCurrencyStock.getAccount().getId(),
                sellCurrencyStock.getAccount().getName()
            ),
            new ReferenceRecord(
                sellCurrencyStock.getTicker().getId(),
                sellCurrencyStock.getTicker().getName()
            ),
            sellCurrencyStock.getPrice(),
            sellCurrencyStock.getCount(),
            sellCurrencyStock.getCurrencyRate(),
            new ReferenceRecord(
                sellCurrencyStock.getCurrency().getId(),
                sellCurrencyStock.getCurrency().getName()
            ),
            new ReferenceRecord(
                sellCurrencyStock.getCommission().getId(),
                sellCurrencyStock.getCommission().getName()
            ),
            sellCurrencyStock.getCommissionAmount(),
            new ReferenceRecord(
                sellCurrencyStock.getStockSaleIncome().getId(),
                sellCurrencyStock.getStockSaleIncome().getName()
            ),
            new ReferenceRecord(
                sellCurrencyStock.getStockSaleExpense().getId(),
                sellCurrencyStock.getStockSaleExpense().getName()
            ),
            sellCurrencyStock.getDirectSelling()
        );
    }

    private SellCurrencyStock toEntity(final SellCurrencyStockRecord sellCurrencyStockRecord) {
        final SellCurrencyStock sellCurrencyStock = new SellCurrencyStock();
        sellCurrencyStock.setName(sellCurrencyStock.getName());
        final Account account = new Account();
        account.setId(sellCurrencyStockRecord.account().id());
        account.setName(sellCurrencyStockRecord.account().name());
        sellCurrencyStock.setAccount(account);
        final TickerSymbol ticker = new TickerSymbol();
        ticker.setId(sellCurrencyStockRecord.ticker().id());
        ticker.setName(sellCurrencyStockRecord.ticker().name());
        sellCurrencyStock.setTicker(ticker);
        sellCurrencyStock.setPrice(sellCurrencyStock.getPrice());
        sellCurrencyStock.setCount(sellCurrencyStock.getCount());
        sellCurrencyStock.setCurrencyRate(sellCurrencyStock.getCurrencyRate());
        final Currency currency = new Currency();
        currency.setId(sellCurrencyStockRecord.currency().id());
        currency.setName(sellCurrencyStockRecord.currency().name());
        sellCurrencyStock.setCurrency(currency);
        final ExpenseItem commission = new ExpenseItem();
        commission.setId(sellCurrencyStockRecord.commission().id());
        commission.setName(sellCurrencyStockRecord.commission().name());
        sellCurrencyStock.setCommission(commission);
        sellCurrencyStock.setCommissionAmount(sellCurrencyStock.getCommissionAmount());
        final IncomeItem stockSaleIncome = new IncomeItem();
        stockSaleIncome.setId(sellCurrencyStockRecord.stockSaleIncome().id());
        stockSaleIncome.setName(sellCurrencyStockRecord.stockSaleIncome().name());
        sellCurrencyStock.setStockSaleIncome(stockSaleIncome);
        final ExpenseItem stockSaleExpense = new ExpenseItem();
        stockSaleExpense.setId(sellCurrencyStockRecord.stockSaleExpense().id());
        stockSaleExpense.setName(sellCurrencyStockRecord.stockSaleExpense().name());
        sellCurrencyStock.setStockSaleExpense(stockSaleExpense);
        sellCurrencyStock.setDirectSelling(sellCurrencyStock.getDirectSelling());
        return sellCurrencyStock;
    }

}