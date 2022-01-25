package old.ledgerserver.platform.entities.document.sellstock;

import javax.transaction.Transactional;

import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.stereotype.Service;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import com.evgenltd.ledgerserver.util.ApplicationException;
import java.lang.String;
import com.evgenltd.ledgerserver.common.entity.Account;
import com.evgenltd.ledgerserver.stonks.entity.TickerSymbol;

import java.lang.Long;
import com.evgenltd.ledgerserver.common.entity.ExpenseItem;
import com.evgenltd.ledgerserver.common.entity.IncomeItem;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellStockService {

    private final SellStockRepository sellStockRepository;

    public SellStockService(
        final SellStockRepository sellStockRepository
    ) {
        this.sellStockRepository = sellStockRepository;
    }

    public long count(final LoadConfig loadConfig) {
        return sellStockRepository.count(loadConfig.toSpecification());
    }

    public List<SellStockRow> list(final LoadConfig loadConfig) {
        return sellStockRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).stream()
                .map(this::toRow)
                .toList();
    }

    public List<ReferenceRecord> filter(final String filter) {
        return sellStockRepository.findAll()
                .stream()
                .filter(sellStock -> filter == null || filter.isBlank() || sellStock.getName().contains(filter))
                .map(this::toReference)
                .collect(Collectors.toList());
    }

    public SellStockRecord byId(final Long id) {
        return sellStockRepository.findById(id)
                .map(this::toRecord)
                .orElseThrow(() -> new ApplicationException("SellStock [%s] not found", id));
    }

    @Transactional
    public void update(final SellStockRecord sellStockRecord) {
        final SellStock sellStock = toEntity(sellStockRecord);
        sellStockRepository.save(sellStock);
    }

    @Transactional
    public void delete(final Long id) {
        sellStockRepository.deleteById(id);
    }

    private SellStockRow toRow(final SellStock sellStock) {
        return new SellStockRow(
            sellStock.getId(),
            sellStock.getName(),
            sellStock.getAccount().getId(),
            sellStock.getAccount().getName(),
            sellStock.getTicker().getId(),
            sellStock.getTicker().getName(),
            sellStock.getPrice(),
            sellStock.getCount(),
            sellStock.getCommission().getId(),
            sellStock.getCommission().getName(),
            sellStock.getCommissionAmount(),
            sellStock.getStockSaleIncome().getId(),
            sellStock.getStockSaleIncome().getName(),
            sellStock.getStockSaleExpense().getId(),
            sellStock.getStockSaleExpense().getName()
        );
    }

    private ReferenceRecord toReference(final SellStock sellStock) {
        return new ReferenceRecord(sellStock.getId(), sellStock.getName());
    }

    private SellStockRecord toRecord(final SellStock sellStock) {
        return new SellStockRecord(
            sellStock.getId(),
            sellStock.getName(),
            new ReferenceRecord(
                sellStock.getAccount().getId(),
                sellStock.getAccount().getName()
            ),
            new ReferenceRecord(
                sellStock.getTicker().getId(),
                sellStock.getTicker().getName()
            ),
            sellStock.getPrice(),
            sellStock.getCount(),
            new ReferenceRecord(
                sellStock.getCommission().getId(),
                sellStock.getCommission().getName()
            ),
            sellStock.getCommissionAmount(),
            new ReferenceRecord(
                sellStock.getStockSaleIncome().getId(),
                sellStock.getStockSaleIncome().getName()
            ),
            new ReferenceRecord(
                sellStock.getStockSaleExpense().getId(),
                sellStock.getStockSaleExpense().getName()
            )
        );
    }

    private SellStock toEntity(final SellStockRecord sellStockRecord) {
        final SellStock sellStock = new SellStock();
        sellStock.setName(sellStock.getName());
        final Account account = new Account();
        account.setId(sellStockRecord.account().id());
        account.setName(sellStockRecord.account().name());
        sellStock.setAccount(account);
        final TickerSymbol ticker = new TickerSymbol();
        ticker.setId(sellStockRecord.ticker().id());
        ticker.setName(sellStockRecord.ticker().name());
        sellStock.setTicker(ticker);
        sellStock.setPrice(sellStock.getPrice());
        sellStock.setCount(sellStock.getCount());
        final ExpenseItem commission = new ExpenseItem();
        commission.setId(sellStockRecord.commission().id());
        commission.setName(sellStockRecord.commission().name());
        sellStock.setCommission(commission);
        sellStock.setCommissionAmount(sellStock.getCommissionAmount());
        final IncomeItem stockSaleIncome = new IncomeItem();
        stockSaleIncome.setId(sellStockRecord.stockSaleIncome().id());
        stockSaleIncome.setName(sellStockRecord.stockSaleIncome().name());
        sellStock.setStockSaleIncome(stockSaleIncome);
        final ExpenseItem stockSaleExpense = new ExpenseItem();
        stockSaleExpense.setId(sellStockRecord.stockSaleExpense().id());
        stockSaleExpense.setName(sellStockRecord.stockSaleExpense().name());
        sellStock.setStockSaleExpense(stockSaleExpense);
        return sellStock;
    }

}