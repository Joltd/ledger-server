package com.evgenltd.ledgerserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BotService extends TelegramLongPollingBot {

    @Value("${bot.token}")
    private String botToken;

    private final Map<String, Handler> handlers = new HashMap<>();

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return "LedgerBot";
    }

    public void registerHandler(final String command, final Handler handler) {
        handlers.put(command, handler);
        execute(new SetMyCommands(
                handlers.keySet()
                        .stream()
                        .map(c -> new BotCommand(c, "stub"))
                        .collect(Collectors.toList())
        ));
    }

    @Override
    public void onUpdateReceived(final Update update) {
        if (update.getMessage() == null) {
            return;
        }

        final Long chatId = update.getMessage().getChatId();
        final String text = update.getMessage().getText();
        if (chatId == null || text == null || text.isBlank()) {
            return;
        }

        final String[] tokens = text.split(" ");
        if (tokens.length == 0) {
            return;
        }

        final String command = tokens[0];
        final String[] arguments = Arrays.copyOfRange(tokens, 1, tokens.length);
        final Handler handler = handlers.get(command);
        if (handler == null) {
            sendMessage(chatId, "Unknown command [%s]", command);
            return;
        }

        handler.handle(update, chatId, arguments);
    }

    public void sendMessage(final Long chatId, final String message, final Object... args) {
        execute(new SendMessage(chatId, String.format(message, args)));
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(final Method method)  {
        try {
            return super.execute(method);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public interface Handler {
        void handle(final Update update, final Long chatId, final String... args);
    }

}
