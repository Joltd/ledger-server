package com.evgenltd.ledgerserver.service.brocker;

import com.evgenltd.ledgerserver.platform.ApplicationException;
import com.evgenltd.ledgerserver.constants.Codes;
import com.evgenltd.ledgerserver.constants.Settings;
import com.evgenltd.ledgerserver.reference.entity.Account;
import com.evgenltd.ledgerserver.reference.entity.ExpenseItem;
import com.evgenltd.ledgerserver.entity.JournalEntry;
import com.evgenltd.ledgerserver.entity.TinkoffTariff;
import com.evgenltd.ledgerserver.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.repository.TinkoffTariffRepository;
import com.evgenltd.ledgerserver.service.SettingService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TinkoffBrokerService implements BrokerService {

    @Value("${broker.host}")
    private String host;

    @Value("${broker.token}")
    private String token;

    private final TinkoffTariffRepository tinkoffTariffRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final SettingService settingService;
    private final ObjectMapper mapper;
    private WebClient client;

    public TinkoffBrokerService(
            final TinkoffTariffRepository tinkoffTariffRepository,
            final JournalEntryRepository journalEntryRepository,
            final SettingService settingService,
            final ObjectMapper mapper
    ) {
        this.tinkoffTariffRepository = tinkoffTariffRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.settingService = settingService;
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
    public BigDecimal calculateCommission(final Account account, final LocalDateTime date, final BigDecimal amount) {
        final Optional<TinkoffTariff> tariffHolder = tinkoffTariffRepository.findTopByAccountAndDateLessThanOrderByDateDesc(account, date);
        if (tariffHolder.isEmpty()) {
            throw new ApplicationException("Unable to calculate commission - tariff not found");
        }

        final TinkoffTariff tariff = tariffHolder.get();

        final String tariffName = tariff.getTariff();

        if (tariffName.equals(Tariff.INVESTOR.name())) {
            return calculateCommissionByInvestor(amount);
        } else if (tariffName.equals(Tariff.TRADER.name())) {
            return calculateCommissionByTrader(tariff, date, amount);
        } else {
            throw new ApplicationException("Unknown tariff [%s]", tariffName);
        }

    }

    @Override
    public BigDecimal rate(final String figi) {

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
            throw new ApplicationException(e, "Unable to retrieve rate for [%s]", figi);
        }
    }

    private BigDecimal calculateCommissionByInvestor(final BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.003"));
    }

    private BigDecimal calculateCommissionByTrader(final TinkoffTariff tariff, final LocalDateTime date, final BigDecimal amount) {

        LocalDateTime from = tariff.getDate()
                .withYear(date.getYear())
                .withMonth(date.getMonthValue());

        if (from.compareTo(date) > 0) {
            from = from.minusMonths(1L);
        }

        final ExpenseItem commission = settingService.get(Settings.BROKER_COMMISSION_EXPENSE_ITEM);

        final List<JournalEntry> commissionEntries = journalEntryRepository.findByDateGreaterThanEqualAndDateLessThanEqualAndCodeAndTypeAndAccountAndExpenseItem(
                        from,
                        date,
                        Codes.C91_2,
                        JournalEntry.Type.DEBIT,
                        tariff.getAccount(),
                        commission
                );

        if (commissionEntries.isEmpty()) {
            return amount.multiply(new BigDecimal("0.0005")).add(new BigDecimal("290"));
        }

        return amount.multiply(new BigDecimal("0.0005"));

    }

    public enum Tariff {
        INVESTOR,
        TRADER
    }

}
