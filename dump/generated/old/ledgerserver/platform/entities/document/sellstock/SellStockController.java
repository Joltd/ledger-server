package old.ledgerserver.platform.entities.document.sellstock;

import java.util.List;

import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.web.bind.annotation.*;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;

@RestController
@RequestMapping("/entity/entities/document/sell-stock/sell-stock")
public class SellStockController {

    private final SellStockService sellStockService;

    public SellStockController(final SellStockService sellStockService) {
        this.sellStockService = sellStockService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return sellStockService.count(loadConfig);
    }

    @PostMapping("/")
    public List<SellStockRow> list(@RequestBody final LoadConfig loadConfig) {
        return sellStockService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return sellStockService.filter(filter);
    }

    @GetMapping("/{id}")
    public SellStockRecord byId(@PathVariable("id") final Long id) {
        return sellStockService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final SellStockRecord sellStockRecord) {
        sellStockService.update(sellStockRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        sellStockService.delete(id);
    }

}