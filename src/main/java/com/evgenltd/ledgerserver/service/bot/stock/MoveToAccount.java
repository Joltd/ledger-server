package com.evgenltd.ledgerserver.service.bot.stock;

import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.entity.Account;
import com.evgenltd.ledgerserver.repository.AccountRepository;
import com.evgenltd.ledgerserver.service.StockService;
import com.evgenltd.ledgerserver.service.bot.BotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MoveToAccount {

    private static final Logger log = LoggerFactory.getLogger(MoveToAccount.class);

    private final BotService botService;
    private final StockService stockService;
    private final AccountRepository accountRepository;

    public MoveToAccount(
            final BotService botService,
            final StockService stockService,
            final AccountRepository accountRepository
    ) {
        this.botService = botService;
        this.stockService = stockService;
        this.accountRepository = accountRepository;
    }

    @PostConstruct
    public void postConstruct() {
        botService.registerHandler("/movetoaccount", this::mainHandler);
    }

    private void mainHandler(final Update update, final Long chatId, final String... args) {
        if (args.length < 3) {
            botService.sendMessage(chatId, "/moveToAccount <yyyy-MM-dd> <account from> <account to> <amount>");
            return;
        }

        try {
            final LocalDateTime date = LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            final Account from = accountRepository.findByName(args[1]).orElse(null);
            final Account to = accountRepository.findByName(args[2]).orElse(null);
            final BigDecimal amount = new BigDecimal(args[3]);

            stockService.moveToAccount(date, from, to, amount);

            botService.sendMessage(chatId, "Done");
        } catch (final Exception e) {
            botService.sendMessage(chatId, "Failed, %s", e.getMessage());
            log.error(e.getMessage(), e);
        }
    }

}
