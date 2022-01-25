package old.ledgerserver.platform.entities.document.buystock;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;

@RestController
@RequestMapping("/entity/entities/document/buy-stock/buy-stock")
public class BuyStockController {

    private final BuyStockService buyStockService;

    public BuyStockController(final BuyStockService buyStockService) {
        this.buyStockService = buyStockService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return buyStockService.count(loadConfig);
    }

    @PostMapping("/")
    public List<BuyStockRow> list(@RequestBody final LoadConfig loadConfig) {
        return buyStockService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return buyStockService.filter(filter);
    }

    @GetMapping("/{id}")
    public BuyStockRecord byId(@PathVariable("id") final Long id) {
        return buyStockService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final BuyStockRecord buyStockRecord) {
        buyStockService.update(buyStockRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        buyStockService.delete(id);
    }

}