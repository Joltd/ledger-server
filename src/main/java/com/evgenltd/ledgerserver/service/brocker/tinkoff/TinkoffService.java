package com.evgenltd.ledgerserver.service.brocker.tinkoff;

import com.evgenltd.ledgerserver.entity.Currency;
import com.evgenltd.ledgerserver.entity.TickerSymbol;
import com.evgenltd.ledgerserver.service.brocker.Market;
import com.evgenltd.ledgerserver.util.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Service
public class TinkoffService implements Market {

    @Value("${broker.host}")
    private String host;

    @Value("${broker.token}")
    private String token;

    private WebClient client;

    private final ObjectMapper mapper;

    public TinkoffService(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    public void postConstruct() {
        client = WebClient.builder()
                .baseUrl(host)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    @Override
    public BigDecimal price(final String figi) {

        if (Utils.isBlank(figi)) {
            return BigDecimal.ZERO;
        }

        final String result = client.get()
                .uri(uriBuilder -> uriBuilder.path("/market/orderbook")
                        .queryParam("figi", figi)
                        .queryParam("depth", 1)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            final JsonNode tree = mapper.readTree(result);
            final JsonNode payload = tree.get("payload");
            final ArrayNode bids = (ArrayNode) payload.get("bids");
            if (bids.isEmpty()) {
                final String price = payload.get("lastPrice").asText();
                return new BigDecimal(price);
            }
            final String price = bids.get(0).get("price").asText();
            return new BigDecimal(price);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
