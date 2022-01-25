package old.ledgerserver.controller;

import old.ledgerserver.service.StockService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;

    public StockController(final StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/portfolio")
    public StockService.PortfolioRecord portfolio() {
        return stockService.collectPortfolioAnalysis();
    }

}
