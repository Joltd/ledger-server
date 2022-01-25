package old.ledgerserver.platform.entities.document.buycurrencystock;

import java.util.List;

import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.web.bind.annotation.*;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;

@RestController
@RequestMapping("/entity/entities/document/buy-currency-stock/buy-currency-stock")
public class BuyCurrencyStockController {

    private final BuyCurrencyStockService buyCurrencyStockService;

    public BuyCurrencyStockController(final BuyCurrencyStockService buyCurrencyStockService) {
        this.buyCurrencyStockService = buyCurrencyStockService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return buyCurrencyStockService.count(loadConfig);
    }

    @PostMapping("/")
    public List<BuyCurrencyStockRow> list(@RequestBody final LoadConfig loadConfig) {
        return buyCurrencyStockService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return buyCurrencyStockService.filter(filter);
    }

    @GetMapping("/{id}")
    public BuyCurrencyStockRecord byId(@PathVariable("id") final Long id) {
        return buyCurrencyStockService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final BuyCurrencyStockRecord buyCurrencyStockRecord) {
        buyCurrencyStockService.update(buyCurrencyStockRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        buyCurrencyStockService.delete(id);
    }

}