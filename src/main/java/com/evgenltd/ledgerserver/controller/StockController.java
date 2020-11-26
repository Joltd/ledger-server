package com.evgenltd.ledgerserver.controller;

import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import com.evgenltd.ledgerserver.service.StockService;
import com.evgenltd.ledgerserver.util.Utils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;

    public StockController(final StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/portfolio")
    public List<AnalysisRecord> portfolio() {
        return stockService.collectPortfolioAnalysis()
                .stream()
                .map(analysis -> new AnalysisRecord(
                        analysis.getAccount().getId(),
                        analysis.getAccount().getName(),
                        Utils.ifNull(analysis.getTicker(), TickerSymbol::getName, null),
                        analysis.getCount(),
                        analysis.getPrice(),
                        analysis.getCurrency(),
                        analysis.getCurrencyAmount(),
                        analysis.getCurrencyRate(),
                        analysis.getAmount(),
                        analysis.getBalance(),
                        analysis.getTimeWeightedAmount(),
                        analysis.getIncome(),
                        analysis.getProfitability(),
                        analysis.getCommission()
                ))
                .sorted(Comparator.comparing(record -> record.ticker() != null ? record.ticker() : record.currency().name()))
                .collect(Collectors.toList());
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record AnalysisRecord(
            Long accountId,
            String accountName,
            String ticker,
            Long count,
            BigDecimal price,
            Currency currency,
            BigDecimal currencyAmount,
            BigDecimal currencyRate,
            BigDecimal amount,
            BigDecimal balance,
            BigDecimal timeWeightedAmount,
            BigDecimal income,
            BigDecimal profitability,
            BigDecimal commission
    ) {}

}
