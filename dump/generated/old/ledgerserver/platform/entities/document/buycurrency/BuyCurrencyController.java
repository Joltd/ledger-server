package old.ledgerserver.platform.entities.document.buycurrency;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.evgenltd.ledgerserver.util.ReferenceRecord;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;

@RestController
@RequestMapping("/entity/entities/document/buy-currency/buy-currency")
public class BuyCurrencyController {

    private final BuyCurrencyService buyCurrencyService;

    public BuyCurrencyController(final BuyCurrencyService buyCurrencyService) {
        this.buyCurrencyService = buyCurrencyService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return buyCurrencyService.count(loadConfig);
    }

    @PostMapping("/")
    public List<BuyCurrencyRow> list(@RequestBody final LoadConfig loadConfig) {
        return buyCurrencyService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return buyCurrencyService.filter(filter);
    }

    @GetMapping("/{id}")
    public BuyCurrencyRecord byId(@PathVariable("id") final Long id) {
        return buyCurrencyService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final BuyCurrencyRecord buyCurrencyRecord) {
        buyCurrencyService.update(buyCurrencyRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        buyCurrencyService.delete(id);
    }

}