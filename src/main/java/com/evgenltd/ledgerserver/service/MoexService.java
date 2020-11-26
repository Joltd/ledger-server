package com.evgenltd.ledgerserver.service;

import com.evgenltd.ledgerserver.ApplicationException;
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

@Service
public class MoexService implements StockExchangeService {

    private static final Logger log = LoggerFactory.getLogger(MoexService.class);

    private final ObjectMapper mapper;
    private WebClient client;

    public MoexService(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    public void postConstruct() {
        client = WebClient.builder()
                .baseUrl("http://iss.moex.com/iss")
                .build();
    }

    @Override
    public BigDecimal rate(final LocalDate date, final String ticker) {
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

            final String rate = history.get(0).get("LEGALCLOSEPRICE").asText();
            return new BigDecimal(rate);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e, "Unable to retrieve rate for [%s][%s]", date, ticker);
        }
    }

}
