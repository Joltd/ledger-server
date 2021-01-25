package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.StockPrice;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import com.evgenltd.ledgerserver.repository.StockPriceRepository;
import com.evgenltd.ledgerserver.repository.TickerSymbolRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StockPriceHistoryService {

    private static final LocalDate HISTORY_FROM = LocalDate.of(2020, 1, 3);
    private final TickerSymbolRepository tickerSymbolRepository;
    private final StockExchangeService stockExchangeService;
    private final StockPriceRepository stockPriceRepository;

    public StockPriceHistoryService(
            final TickerSymbolRepository tickerSymbolRepository,
            final StockExchangeService stockExchangeService,
            final StockPriceRepository stockPriceRepository
    ) {
        this.tickerSymbolRepository = tickerSymbolRepository;
        this.stockExchangeService = stockExchangeService;
        this.stockPriceRepository = stockPriceRepository;
    }

    @PostConstruct
    public void postConstruct() {
        stockPriceRepository.findAll()
                .stream()
                .filter(stockPrice -> stockPrice.getPrice() == null)
                .forEach(stockPriceRepository::delete);
        final Map<Key, BigDecimal> index = stockPriceRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        entry -> new Key(entry.getDate(), entry.getTicker()),
                        StockPrice::getPrice
                ));
        final List<String> tickers = tickers();
        Stream.iterate(HISTORY_FROM, date -> date.plusDays(1L))
                .limit(ChronoUnit.DAYS.between(HISTORY_FROM, LocalDate.now().minusDays(1L)))
                .forEach(date -> tickers
                        .stream()
                        .filter(ticker -> !index.containsKey(new Key(date, ticker)))
                        .forEach(ticker -> {
                            BigDecimal rate = stockExchangeService.rate(date, ticker);
                            if (rate.compareTo(BigDecimal.ZERO) == 0) {
                                rate = index.get(new Key(date.minusDays(1L), ticker));
                            }
                            saveStockPriceInfo(date, ticker, rate);
                            index.put(new Key(date, ticker), rate);
                        })
                );
    }

    @Scheduled(cron = "0 0 * * * *", zone = "Europe/Samara")
    public void gatherPrices() {
        tickers().forEach(ticker -> {
            final LocalDate date = LocalDate.now();
            final StockPrice stockPrice = stockPriceRepository.findByDateAndTicker(date, ticker)
                    .orElseGet(() -> {
                        final StockPrice sp = new StockPrice();
                        sp.setDate(date);
                        sp.setTicker(ticker);
                        return sp;
                    });
            final BigDecimal rate = stockExchangeService.rate(ticker);
            if (BigDecimal.ZERO.compareTo(rate) != 0) {
                stockPrice.setPrice(rate);
                stockPriceRepository.save(stockPrice);
            }
        });
    }

    private List<String> tickers() {
        final List<String> tickers = tickerSymbolRepository.findAll()
                .stream()
                .map(TickerSymbol::getName)
                .collect(Collectors.toList());
        tickers.add(Currency.USD.name());
        return tickers;
    }

    private void saveStockPriceInfo(final LocalDate date, final String ticker, final BigDecimal rate) {
        final StockPrice stockPrice = new StockPrice();
        stockPrice.setDate(date);
        stockPrice.setTicker(ticker);
        stockPrice.setPrice(rate);
        stockPriceRepository.save(stockPrice);
    }

    private static record Key(LocalDate date, String ticker) {}

}
