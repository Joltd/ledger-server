package com.evgenltd.ledgerserver.service.bot.activity;

import com.evgenltd.ledgerserver.service.StockExchangeService;
import com.evgenltd.ledgerserver.service.bot.BotActivity;
import com.evgenltd.ledgerserver.util.Tokenizer;
import com.evgenltd.ledgerserver.util.Utils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static com.evgenltd.ledgerserver.service.bot.BotState.sendMessage;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RateCacheActivity extends BotActivity {

    private final StockExchangeService stockExchangeService;

    public RateCacheActivity(final StockExchangeService stockExchangeService) {
        this.stockExchangeService = stockExchangeService;
        command(this::print, "print", "p", "view", "v");
        command(this::clear, "clear", "c", "remove", "r", "drop", "d");
    }

    private void print(final Tokenizer tokenizer) {
        final String result = stockExchangeService.getRateCache()
                .entrySet()
                .stream()
                .map(entry -> String.format("%s - %s", entry.getKey(), Utils.formatMoney(entry.getValue())))
                .collect(Collectors.joining("\n"));
        if (Utils.isBlank(result)) {
            sendMessage("Cache is empty");
        } else {
            sendMessage(result);
        }
    }

    private void clear(final Tokenizer tokenizer) {
        stockExchangeService.clearCache();
        print(tokenizer);
    }

}
