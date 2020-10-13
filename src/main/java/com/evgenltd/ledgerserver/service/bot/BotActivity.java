package com.evgenltd.ledgerserver.service.bot;

import org.springframework.beans.factory.BeanFactory;

public abstract class BotActivity {

    private final BotService botService;

    private Long chatId;

    public BotActivity(final BotService botService) {
        this.botService = botService;
    }

    public void setChatId(final Long chatId) {
        this.chatId = chatId;
    }

    protected void sendMessage(final String message, final Object... args) {
        botService.sendMessage(chatId, String.format(message, args));
    }

    protected void activityNew(final BotActivity botActivity) {
        botService.activityNew(chatId, botActivity);
    }

    protected void activityBack() {
        botService.activityBack(chatId);
    }

    public void messageReceived(final String message) {
        if (message.toLowerCase().contains("where am i")) {
            sendMessage(getClass().getSimpleName());
            return;
        } else if (message.toLowerCase().contains("done")) {
            activityBack();
            return;
        }
        onMessageReceived(message);
    }

    protected abstract void onMessageReceived(final String message);

    protected void hello() {
        sendMessage(getClass().getSimpleName());
    }

    protected FirstWord splitFirstWord(final String message) {
        final int index = message.indexOf(" ");
        if (index < 0) {
            return new FirstWord(message, "");
        }

        return new FirstWord(message.substring(0, index).toLowerCase(), message.substring(index + 1));
    }

    public static record FirstWord(String word, String message) {}

}
