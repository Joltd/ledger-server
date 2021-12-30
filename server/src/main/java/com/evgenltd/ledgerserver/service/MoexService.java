package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.platform.common.ApplicationException;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class MoexService implements StockExchangeService {

    private static final Logger log = LoggerFactory.getLogger(MoexService.class);

    private final ObjectMapper mapper;
    private WebClient client;
    private final Map<String,String> tickerToBoard = new HashMap<>();
    private final Map<String,String> currencyToCid = new HashMap<>();

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
    public BigDecimal rate(final String ticker) {
        return rate(null, ticker);
    }

    @Override
    public BigDecimal rate(final LocalDate date, final String ticker) {
        final boolean isCurrency = currencyToCid.containsKey(ticker);
        if (date == null) {
            return isCurrency
                    ? rateCurrency(ticker)
                    : rateStock(ticker);
        } else {
            return isCurrency
                    ? rateCurrencyHistory(date, ticker)
                    : rateStockHistory(date, ticker);
        }

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
        final String board = boardByTicker(ticker);

        final String result = client.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("history", "engines", "stock", "markets", "shares", "boards", board, "securities", ticker, "securities.json")
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

            return new BigDecimal(history.get(0).get("LEGALCLOSEPRICE").asText());
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

            return new BigDecimal(history.get(0).get("CLOSE").asText());
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e, "Unable to retrieve rate for [%s][%s]", date, ticker);
        }
    }

    private String boardByTicker(final String ticker) {
        return tickerToBoard.getOrDefault(ticker, "TQTF");
    }

}
