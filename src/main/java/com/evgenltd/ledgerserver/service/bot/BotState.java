package com.evgenltd.ledgerserver.service.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

public class BotState {

    private static final Logger log = LoggerFactory.getLogger(BotState.class);

    private static final ThreadLocal<Context> context = new ThreadLocal<>();
    private static BotService botService;

    public static void setup(final BotService botService) {
        BotState.botService = botService;
    }

    public static void setup(final Long chatId) {
        get().setChatId(chatId);
    }

    public static void reset() {
        context.remove();
    }

    public static String chatId() {
        return String.valueOf(get().getChatId());
    }

    public static void sendMessage(final String message, final Object... args) {
        Optional.ofNullable(get().getChatId())
                .ifPresentOrElse(
                        chatId -> botService.sendMessage(chatId, message, args),
                        () -> log.info(String.format(message, args))
                );
    }

    public static void activityNew(final BotActivity activity) {
        Optional.ofNullable(get().getChatId())
                .ifPresentOrElse(
                        chatId -> botService.activityNew(chatId, activity),
                        () -> log.info("Chat is unknown")
                );
    }

    public static void activityBack() {
        Optional.ofNullable(get().getChatId())
                .ifPresentOrElse(
                        chatId -> botService.activityBack(chatId),
                        () -> log.info("Chat is unknown")
                );
    }

    private static Context get() {
        if (context.get() == null) {
            context.set(new Context());
        }
        return context.get();
    }

    private static class Context {
        private Long chatId;

        public Long getChatId() {
            return chatId;
        }

        public void setChatId(final Long chatId) {
            this.chatId = chatId;
        }
    }

}
