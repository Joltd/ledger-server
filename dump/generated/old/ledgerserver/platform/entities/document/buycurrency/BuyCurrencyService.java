package old.ledgerserver.platform.entities.document.buycurrency;

import javax.transaction.Transactional;

import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.stereotype.Service;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;
import com.evgenltd.ledgerserver.util.ApplicationException;
import java.lang.String;

import com.evgenltd.ledgerserver.common.entity.Account;
import old.ledgerserver.platform.entities.reference.currency.Currency;
import com.evgenltd.ledgerserver.common.entity.ExpenseItem;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyCurrencyService {

    private final BuyCurrencyRepository buyCurrencyRepository;

    public BuyCurrencyService(
        final BuyCurrencyRepository buyCurrencyRepository
    ) {
        this.buyCurrencyRepository = buyCurrencyRepository;
    }

    public long count(final LoadConfig loadConfig) {
        return buyCurrencyRepository.count(loadConfig.toSpecification());
    }

    public List<BuyCurrencyRow> list(final LoadConfig loadConfig) {
        return buyCurrencyRepository.findAll(
                loadConfig.toSpecification(),
                loadConfig.toPageRequest()
        ).stream()
                .map(this::toRow)
                .toList();
    }

    public List<ReferenceRecord> filter(final String filter) {
        return buyCurrencyRepository.findAll()
                .stream()
                .filter(buyCurrency -> filter == null || filter.isBlank() || buyCurrency.getName().contains(filter))
                .map(this::toReference)
                .collect(Collectors.toList());
    }

    public BuyCurrencyRecord byId(final Long id) {
        return buyCurrencyRepository.findById(id)
                .map(this::toRecord)
                .orElseThrow(() -> new ApplicationException("BuyCurrency [%s] not found", id));
    }

    @Transactional
    public void update(final BuyCurrencyRecord buyCurrencyRecord) {
        final BuyCurrency buyCurrency = toEntity(buyCurrencyRecord);
        buyCurrencyRepository.save(buyCurrency);
    }

    @Transactional
    public void delete(final Long id) {
        buyCurrencyRepository.deleteById(id);
    }

    private BuyCurrencyRow toRow(final BuyCurrency buyCurrency) {
        return new BuyCurrencyRow(
            buyCurrency.getId(),
            buyCurrency.getName(),
            buyCurrency.getAmount(),
            buyCurrency.getAccount().getId(),
            buyCurrency.getAccount().getName(),
            buyCurrency.getCurrency().getId(),
            buyCurrency.getCurrency().getName(),
            buyCurrency.getCurrencyRate(),
            buyCurrency.getCurrencyAmount(),
            buyCurrency.getCommission().getId(),
            buyCurrency.getCommission().getName(),
            buyCurrency.getCommissionAmount()
        );
    }

    private ReferenceRecord toReference(final BuyCurrency buyCurrency) {
        return new ReferenceRecord(buyCurrency.getId(), buyCurrency.getName());
    }

    private BuyCurrencyRecord toRecord(final BuyCurrency buyCurrency) {
        return new BuyCurrencyRecord(
            buyCurrency.getId(),
            buyCurrency.getName(),
            buyCurrency.getAmount(),
            new ReferenceRecord(
                buyCurrency.getAccount().getId(),
                buyCurrency.getAccount().getName()
            ),
            new ReferenceRecord(
                buyCurrency.getCurrency().getId(),
                buyCurrency.getCurrency().getName()
            ),
            buyCurrency.getCurrencyRate(),
            buyCurrency.getCurrencyAmount(),
            new ReferenceRecord(
                buyCurrency.getCommission().getId(),
                buyCurrency.getCommission().getName()
            ),
            buyCurrency.getCommissionAmount()
        );
    }

    private BuyCurrency toEntity(final BuyCurrencyRecord buyCurrencyRecord) {
        final BuyCurrency buyCurrency = new BuyCurrency();
        buyCurrency.setName(buyCurrency.getName());
        buyCurrency.setAmount(buyCurrency.getAmount());
        final Account account = new Account();
        account.setId(buyCurrencyRecord.account().id());
        account.setName(buyCurrencyRecord.account().name());
        buyCurrency.setAccount(account);
        final Currency currency = new Currency();
        currency.setId(buyCurrencyRecord.currency().id());
        currency.setName(buyCurrencyRecord.currency().name());
        buyCurrency.setCurrency(currency);
        buyCurrency.setCurrencyRate(buyCurrency.getCurrencyRate());
        buyCurrency.setCurrencyAmount(buyCurrency.getCurrencyAmount());
        final ExpenseItem commission = new ExpenseItem();
        commission.setId(buyCurrencyRecord.commission().id());
        commission.setName(buyCurrencyRecord.commission().name());
        buyCurrency.setCommission(commission);
        buyCurrency.setCommissionAmount(buyCurrency.getCommissionAmount());
        return buyCurrency;
    }

}