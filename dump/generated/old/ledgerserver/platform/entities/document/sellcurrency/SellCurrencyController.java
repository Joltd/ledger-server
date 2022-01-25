package old.ledgerserver.platform.entities.document.sellcurrency;

import java.util.List;

import com.evgenltd.ledgerserver.util.ReferenceRecord;
import org.springframework.web.bind.annotation.*;
import com.evgenltd.ledgerserver.util.filter.LoadConfig;

@RestController
@RequestMapping("/entity/entities/document/sell-currency/sell-currency")
public class SellCurrencyController {

    private final SellCurrencyService sellCurrencyService;

    public SellCurrencyController(final SellCurrencyService sellCurrencyService) {
        this.sellCurrencyService = sellCurrencyService;
    }

    @PostMapping("/count")
    public long count(@RequestBody final LoadConfig loadConfig) {
        return sellCurrencyService.count(loadConfig);
    }

    @PostMapping("/")
    public List<SellCurrencyRow> list(@RequestBody final LoadConfig loadConfig) {
        return sellCurrencyService.list(loadConfig);
    }

    @GetMapping("/")
    public List<ReferenceRecord> filter(final String filter) {
        return sellCurrencyService.filter(filter);
    }

    @GetMapping("/{id}")
    public SellCurrencyRecord byId(@PathVariable("id") final Long id) {
        return sellCurrencyService.byId(id);
    }

    @PostMapping
    public void update(@RequestBody final SellCurrencyRecord sellCurrencyRecord) {
        sellCurrencyService.update(sellCurrencyRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") final Long id) {
        sellCurrencyService.delete(id);
    }

}