package old.ledgerserver.platform.entities.document.buystock;

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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyStockService {

    private final BuyStockRepository buyStockRepository;

    public BuyStockService(
        final BuyStockRepository buyStockRepository
    ) {
        this.buyStockRepository = buyStockRepository;
    }

    public long count(final LoadConfig loadConfig) {
        return buyStockRepository.count(loadConfig.toSpecification());
    }

    public List<BuyStockRow> list(final LoadConfig loadConfig) {
        return buyStockRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).stream()
                .map(this::toRow)
                .toList();
    }

    public List<ReferenceRecord> filter(final String filter) {
        return buyStockRepository.findAll()
                .stream()
                .filter(buyStock -> filter == null || filter.isBlank() || buyStock.getName().contains(filter))
                .map(this::toReference)
                .collect(Collectors.toList());
    }

    public BuyStockRecord byId(final Long id) {
        return buyStockRepository.findById(id)
                .map(this::toRecord)
                .orElseThrow(() -> new ApplicationException("BuyStock [%s] not found", id));
    }

    @Transactional
    public void update(final BuyStockRecord buyStockRecord) {
        final BuyStock buyStock = toEntity(buyStockRecord);
        buyStockRepository.save(buyStock);
    }

    @Transactional
    public void delete(final Long id) {
        buyStockRepository.deleteById(id);
    }

    private BuyStockRow toRow(final BuyStock buyStock) {
        return new BuyStockRow(
            buyStock.getId(),
            buyStock.getName(),
            buyStock.getAmount(),
            buyStock.getAccount().getId(),
            buyStock.getAccount().getName(),
            buyStock.getTicker().getId(),
            buyStock.getTicker().getName(),
            buyStock.getPrice(),
            buyStock.getCount(),
            buyStock.getCommission().getId(),
            buyStock.getCommission().getName(),
            buyStock.getCommissionAmount()
        );
    }

    private ReferenceRecord toReference(final BuyStock buyStock) {
        return new ReferenceRecord(buyStock.getId(), buyStock.getName());
    }

    private BuyStockRecord toRecord(final BuyStock buyStock) {
        return new BuyStockRecord(
            buyStock.getId(),
            buyStock.getName(),
            buyStock.getAmount(),
            new ReferenceRecord(
                buyStock.getAccount().getId(),
                buyStock.getAccount().getName()
            ),
            new ReferenceRecord(
                buyStock.getTicker().getId(),
                buyStock.getTicker().getName()
            ),
            buyStock.getPrice(),
            buyStock.getCount(),
            new ReferenceRecord(
                buyStock.getCommission().getId(),
                buyStock.getCommission().getName()
            ),
            buyStock.getCommissionAmount()
        );
    }

    private BuyStock toEntity(final BuyStockRecord buyStockRecord) {
        final BuyStock buyStock = new BuyStock();
        buyStock.setName(buyStock.getName());
        buyStock.setAmount(buyStock.getAmount());
        final Account account = new Account();
        account.setId(buyStockRecord.account().id());
        account.setName(buyStockRecord.account().name());
        buyStock.setAccount(account);
        final TickerSymbol ticker = new TickerSymbol();
        ticker.setId(buyStockRecord.ticker().id());
        ticker.setName(buyStockRecord.ticker().name());
        buyStock.setTicker(ticker);
        buyStock.setPrice(buyStock.getPrice());
        buyStock.setCount(buyStock.getCount());
        final ExpenseItem commission = new ExpenseItem();
        commission.setId(buyStockRecord.commission().id());
        commission.setName(buyStockRecord.commission().name());
        buyStock.setCommission(commission);
        buyStock.setCommissionAmount(buyStock.getCommissionAmount());
        return buyStock;
    }

}