package old.ledgerserver.platform.entities.document.sellcurrencystock;

import java.util.List;

import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.web.bind.annotation.*;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;

@RestController
@RequestMapping("/entity/entities/document/sell-currency-stock/sell-currency-stock")
public class SellCurrencyStockController {

    private final SellCurrencyStockService sellCurrencyStockService;

    public SellCurrencyStockController(final SellCurrencyStockService sellCurrencyStockService) {
        this.sellCurrencyStockService = sellCurrencyStockService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return sellCurrencyStockService.count(loadConfig);
    }

    @PostMapping("/")
    public List<SellCurrencyStockRow> list(@RequestBody final LoadConfig loadConfig) {
        return sellCurrencyStockService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return sellCurrencyStockService.filter(filter);
    }

    @GetMapping("/{id}")
    public SellCurrencyStockRecord byId(@PathVariable("id") final Long id) {
        return sellCurrencyStockService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final SellCurrencyStockRecord sellCurrencyStockRecord) {
        sellCurrencyStockService.update(sellCurrencyStockRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        sellCurrencyStockService.delete(id);
    }

}