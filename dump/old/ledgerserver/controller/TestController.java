package old.ledgerserver.controller;

import old.ledgerserver.service.StockExchangeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class TestController {

    private final StockExchangeService stockExchangeService;

    public TestController(final StockExchangeService stockExchangeService) {
        this.stockExchangeService = stockExchangeService;
    }

    @GetMapping("/test")
    public BigDecimal stockExchangeRate() {
        stockExchangeService.rate("FXUS");
        return stockExchangeService.rate("FXUS");
    }

}
