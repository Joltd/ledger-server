package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.ApplicationException;
import com.evgenltd.ledgerserver.util.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MoexService implements StockExchangeService {

    private static final Logger log = LoggerFactory.getLogger(MoexService.class);

    private final ObjectMapper mapper;
    private WebClient client;
    private final Map<String,String> tickerToBoard = new HashMap<>();
    private final Map<String,String> currencyToCid = new HashMap<>();
    private final Map<Key,BigDecimal> rateCache = new ConcurrentHashMap<>();

    public MoexService(final ObjectMapper mapper) {
        this.mapper = mapper;
        tickerToBoard.put("TUSD", "TQTD");
        currencyToCid.put("USD", "USD000UTSTOM");
        currencyToCid.put("EUR", "EUR_RUB__TOM");
    }

    @PostConstruct
    public void postConstruct() {
        client = WebClient.builder()
                .baseUrl("http://iss.moex.com/iss")
                .build();
    }

    @Override
    public Map<Key, BigDecimal> getRateCache() {
        return Collections.unmodifiableMap(rateCache);
    }

    @Override
    public void clearCache() {
        rateCache.clear();
    }

    @Override
    public BigDecimal rate(final String ticker) {

        final boolean isCurrency = currencyToCid.containsKey(ticker);
        final BigDecimal rate = isCurrency
                ? rateCurrency(ticker)
                : rateStock(ticker);
        if (rate.compareTo(BigDecimal.ZERO) != 0) {
            return rate;
        }

        LocalDate day = LocalDate.now().minusDays(1L);

        while (true) {
            final BigDecimal rateHistory = isCurrency
                    ? rateCurrencyHistory(day, ticker)
                    : rateStockHistory(day, ticker);
            if (rateHistory.compareTo(BigDecimal.ZERO) == 0) {
                day = day.minusDays(1L);
                continue;
            }

            return rateHistory;
        }

    }

    @Override
    public BigDecimal rate(final LocalDate date, final String ticker) {
        return null;
    }

    private BigDecimal rateStock(final String ticker) {
        final String board = boardByTicker(ticker);

        final String result = client.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("engines", "stock", "markets", "shares", "boards", board, "securities", ticker, "securities.json")
                        .queryParam("iss.meta", "off")
                        .queryParam("iss.json", "extended")
                        .queryParam("history.columns", "TRADEDATE,SECID,LEGALCLOSEPRICE")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info(result);

        try {
            final JsonNode tree = mapper.readTree(result);
            if (tree.size() < 2) {
                log.warn("Unexpected response");
                return BigDecimal.ZERO;
            }

            final JsonNode marketdata = tree.get(1).get("marketdata");
            if (marketdata.isEmpty()) {
                log.warn("Unexpected response");
                return BigDecimal.ZERO;
            }

            final JsonNode data = marketdata.get(0);
            return Utils.asBigDecimalNoThrow(data.get("LAST").asText()).orElse(BigDecimal.ZERO);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e, "Unable to retrieve rate for [%s]", ticker);
        }
    }

    private BigDecimal rateStockHistory(final LocalDate date, final String ticker) {
        final Key key = new Key(ticker, date);
        final BigDecimal cached = rateCache.get(key);
        if (cached != null) {
            return cached;
        }

        final String result = client.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("history", "engines", "stock", "markets", "shares", "boards", "TQTF", "securities", ticker, "securities.json")
                        .queryParam("iss.meta", "off")
                        .queryParam("iss.json", "extended")
                        .queryParam("from", date.toString())
                        .queryParam("till", date.toString())
                        .queryParam("history.columns", "TRADEDATE,SECID,LEGALCLOSEPRICE")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info(result);

        try {
            final JsonNode tree = mapper.readTree(result);
            if (tree.size() < 2) {
                return BigDecimal.ZERO;
            }
            final JsonNode history = tree.get(1).get("history");
            if (history.isEmpty()) {
                return BigDecimal.ZERO;
            }

            final BigDecimal rate = new BigDecimal(history.get(0).get("LEGALCLOSEPRICE").asText());
            rateCache.put(key, rate);
            return rate;
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e, "Unable to retrieve rate for [%s][%s]", date, ticker);
        }
    }

    private BigDecimal rateCurrency(final String ticker) {
        final String cid = currencyToCid.get(ticker);
        if (Utils.isBlank(cid)) {
            throw new ApplicationException("Unknown currency [%s]", ticker);
        }

        final String result = client.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("engines", "currency", "markets", "selt", "boards", "CETS", "securities", cid, "securities.json")
                        .queryParam("iss.meta", "off")
                        .queryParam("iss.json", "extended")
                        .queryParam("history.columns", "LAST")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info(result);

        try {
            final JsonNode tree = mapper.readTree(result);
            if (tree.size() < 2) {
                return BigDecimal.ZERO;
            }
            final JsonNode marketdata = tree.get(1).get("marketdata");
            if (marketdata.isEmpty()) {
                return BigDecimal.ZERO;
            }

            return Utils.asBigDecimalNoThrow(marketdata.get(0).get("LAST").asText()).orElse(BigDecimal.ZERO);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e, "Unable to retrieve rate for [%s]", ticker);
        }
    }


    private BigDecimal rateCurrencyHistory(final LocalDate date, final String ticker) {
        final String cid = currencyToCid.get(ticker);
        if (Utils.isBlank(cid)) {
            throw new ApplicationException("Unknown currency [%s]", ticker);
        }

        final Key key = new Key(ticker, LocalDate.now());
        final BigDecimal cached = this.rateCache.get(key);
        if (cached != null) {
            return cached;
        }

        final String result = client.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("history", "engines", "currency", "markets", "selt", "boards", "CETS", "securities", cid, "securities.json")
                        .queryParam("iss.meta", "off")
                        .queryParam("iss.json", "extended")
                        .queryParam("from", date.toString())
                        .queryParam("till", date.toString())
                        .queryParam("history.columns", "CLOSE")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info(result);

        try {
            final JsonNode tree = mapper.readTree(result);
            if (tree.size() < 2) {
                return BigDecimal.ZERO;
            }
            final JsonNode history = tree.get(1).get("history");
            if (history.isEmpty()) {
                return BigDecimal.ZERO;
            }

            final BigDecimal rate = new BigDecimal(history.get(0).get("CLOSE").asText());
            rateCache.put(key, rate);
            return rate;
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e, "Unable to retrieve rate for [%s][%s]", date, ticker);
        }
    }

    private String boardByTicker(final String ticker) {
        return tickerToBoard.getOrDefault(ticker, "TQTF");
    }

}
