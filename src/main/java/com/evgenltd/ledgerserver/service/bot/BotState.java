package com.evgenltd.ledgerserver.service.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class BotState {

    private static final Logger log = LoggerFactory.getLogger(BotState.class);

    private static final ThreadLocal<Long> context = new ThreadLocal<>();
    private static BotService botService;

    public static void setup(final BotService botService) {
        BotState.botService = botService;
    }

    public static void setup(final Long chatId) {
        context.set(chatId);
    }

    public static void reset() {
        context.remove();
    }

    public static void sendMessage(final String message, final Object... args) {
        Optional.ofNullable(context.get())
                .ifPresentOrElse(
                        chatId -> botService.sendMessage(context.get(), message, args),
                        () -> log.info(String.format(message, args))
                );
    }

    public static void activityNew(final BotActivity activity) {
        Optional.ofNullable(context.get())
                .ifPresentOrElse(
                        chatId -> botService.activityNew(chatId, activity),
                        () -> log.info("Chat is unknown")
                );
    }

    public static void activityBack() {
        Optional.ofNullable(context.get())
                .ifPresentOrElse(
                        chatId -> botService.activityBack(chatId),
                        () -> log.info("Chat is unknown")
                );
    }
}
