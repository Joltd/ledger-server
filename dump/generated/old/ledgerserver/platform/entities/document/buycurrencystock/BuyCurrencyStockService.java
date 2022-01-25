package old.ledgerserver.platform.entities.document.buycurrencystock;

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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyCurrencyStockService {

    private final BuyCurrencyStockRepository buyCurrencyStockRepository;

    public BuyCurrencyStockService(
        final BuyCurrencyStockRepository buyCurrencyStockRepository
    ) {
        this.buyCurrencyStockRepository = buyCurrencyStockRepository;
    }

    public long count(final LoadConfig loadConfig) {
        return buyCurrencyStockRepository.count(loadConfig.toSpecification());
    }

    public List<BuyCurrencyStockRow> list(final LoadConfig loadConfig) {
        return buyCurrencyStockRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).stream()
                .map(this::toRow)
                .toList();
    }

    public List<ReferenceRecord> filter(final String filter) {
        return buyCurrencyStockRepository.findAll()
                .stream()
                .filter(buyCurrencyStock -> filter == null || filter.isBlank() || buyCurrencyStock.getName().contains(filter))
                .map(this::toReference)
                .collect(Collectors.toList());
    }

    public BuyCurrencyStockRecord byId(final Long id) {
        return buyCurrencyStockRepository.findById(id)
                .map(this::toRecord)
                .orElseThrow(() -> new ApplicationException("BuyCurrencyStock [%s] not found", id));
    }

    @Transactional
    public void update(final BuyCurrencyStockRecord buyCurrencyStockRecord) {
        final BuyCurrencyStock buyCurrencyStock = toEntity(buyCurrencyStockRecord);
        buyCurrencyStockRepository.save(buyCurrencyStock);
    }

    @Transactional
    public void delete(final Long id) {
        buyCurrencyStockRepository.deleteById(id);
    }

    private BuyCurrencyStockRow toRow(final BuyCurrencyStock buyCurrencyStock) {
        return new BuyCurrencyStockRow(
            buyCurrencyStock.getId(),
            buyCurrencyStock.getName(),
            buyCurrencyStock.getAccount().getId(),
            buyCurrencyStock.getAccount().getName(),
            buyCurrencyStock.getTicker().getId(),
            buyCurrencyStock.getTicker().getName(),
            buyCurrencyStock.getPrice(),
            buyCurrencyStock.getCount(),
            buyCurrencyStock.getCurrency().getId(),
            buyCurrencyStock.getCurrency().getName(),
            buyCurrencyStock.getCurrencyAmount()
        );
    }

    private ReferenceRecord toReference(final BuyCurrencyStock buyCurrencyStock) {
        return new ReferenceRecord(buyCurrencyStock.getId(), buyCurrencyStock.getName());
    }

    private BuyCurrencyStockRecord toRecord(final BuyCurrencyStock buyCurrencyStock) {
        return new BuyCurrencyStockRecord(
            buyCurrencyStock.getId(),
            buyCurrencyStock.getName(),
            new ReferenceRecord(
                buyCurrencyStock.getAccount().getId(),
                buyCurrencyStock.getAccount().getName()
            ),
            new ReferenceRecord(
                buyCurrencyStock.getTicker().getId(),
                buyCurrencyStock.getTicker().getName()
            ),
            buyCurrencyStock.getPrice(),
            buyCurrencyStock.getCount(),
            new ReferenceRecord(
                buyCurrencyStock.getCurrency().getId(),
                buyCurrencyStock.getCurrency().getName()
            ),
            buyCurrencyStock.getCurrencyAmount()
        );
    }

    private BuyCurrencyStock toEntity(final BuyCurrencyStockRecord buyCurrencyStockRecord) {
        final BuyCurrencyStock buyCurrencyStock = new BuyCurrencyStock();
        buyCurrencyStock.setName(buyCurrencyStock.getName());
        final Account account = new Account();
        account.setId(buyCurrencyStockRecord.account().id());
        account.setName(buyCurrencyStockRecord.account().name());
        buyCurrencyStock.setAccount(account);
        final TickerSymbol ticker = new TickerSymbol();
        ticker.setId(buyCurrencyStockRecord.ticker().id());
        ticker.setName(buyCurrencyStockRecord.ticker().name());
        buyCurrencyStock.setTicker(ticker);
        buyCurrencyStock.setPrice(buyCurrencyStock.getPrice());
        buyCurrencyStock.setCount(buyCurrencyStock.getCount());
        final Currency currency = new Currency();
        currency.setId(buyCurrencyStockRecord.currency().id());
        currency.setName(buyCurrencyStockRecord.currency().name());
        buyCurrencyStock.setCurrency(currency);
        buyCurrencyStock.setCurrencyAmount(buyCurrencyStock.getCurrencyAmount());
        return buyCurrencyStock;
    }

}