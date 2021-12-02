package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.StockRate;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import com.evgenltd.ledgerserver.repository.StockRateRepository;
import com.evgenltd.ledgerserver.repository.TickerSymbolRepository;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StockRateHistoryService {

    private static final LocalDate HISTORY_FROM = LocalDate.of(2020, 1, 3);
    private final TickerSymbolRepository tickerSymbolRepository;
    private final StockExchangeService stockExchangeService;
    private final StockRateRepository stockRateRepository;

    public StockRateHistoryService(
            final TickerSymbolRepository tickerSymbolRepository,
            final StockExchangeService stockExchangeService,
            final StockRateRepository stockRateRepository
    ) {
        this.tickerSymbolRepository = tickerSymbolRepository;
        this.stockExchangeService = stockExchangeService;
        this.stockRateRepository = stockRateRepository;
    }

    @PostConstruct
    public void postConstruct() {
        stockRateRepository.findAll()
                .stream()
                .filter(stockRate -> stockRate.getRate() == null)
                .forEach(stockRateRepository::delete);
        final Map<Key, BigDecimal> index = stockRateRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        entry -> new Key(entry.getDate(), entry.getTicker()),
                        StockRate::getRate
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

//    @Scheduled(cron = "0 0 4 * * *", zone = "Europe/Samara")
    public void gatherRates() {
        tickers().forEach(ticker -> {
            final LocalDate date = LocalDate.now().minusDays(1L);
            final StockRate stockRate = loadStockRate(date, ticker);
            final BigDecimal rate = stockExchangeService.rate(date, ticker);
            if (BigDecimal.ZERO.compareTo(rate) != 0) {
                stockRate.setRate(rate);
                stockRateRepository.save(stockRate);
            }
        });
    }

    public List<StockRate> loadAll() {
        tickers().forEach(this::currentRate);
        return stockRateRepository.findAll(Sort.by("date"));
    }

    public BigDecimal rate(final String ticker) {
        final BigDecimal rate = currentRate(ticker);
        if (rate.compareTo(BigDecimal.ZERO) != 0) {
            return rate;
        } else {
            return loadPreviousRate(LocalDate.now(), ticker);
        }
    }

    private BigDecimal currentRate(final String ticker) {
        final LocalDate now = LocalDate.now();
        final BigDecimal rate = stockExchangeService.rate(ticker);
        if (rate.compareTo(BigDecimal.ZERO) != 0) {
            final StockRate stockRate = loadStockRate(now, ticker);
            stockRate.setRate(rate);
            stockRateRepository.save(stockRate);
            return rate;
        } else {
            return BigDecimal.ZERO;
        }
    }

    private StockRate loadStockRate(final LocalDate date, final String ticker) {
        return stockRateRepository.findByDateAndTicker(date, ticker)
                .orElseGet(() -> {
                    final StockRate sp = new StockRate();
                    sp.setDate(date);
                    sp.setTicker(ticker);
                    return sp;
                });
    }

    private BigDecimal loadPreviousRate(final LocalDate date, final String ticker) {
        return stockRateRepository.findByDateAndTicker(date.minusDays(1L), ticker)
                .map(StockRate::getRate)
                .orElse(BigDecimal.ZERO);
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
        final StockRate stockRate = new StockRate();
        stockRate.setDate(date);
        stockRate.setTicker(ticker);
        stockRate.setRate(rate);
        stockRateRepository.save(stockRate);
    }

    private static record Key(LocalDate date, String ticker) {}

}
