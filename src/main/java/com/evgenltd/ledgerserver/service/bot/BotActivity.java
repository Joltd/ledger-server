package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.Utils;

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

    void messageReceived(final String message) {
        if (Utils.isSimilar(message, "hey", "?")) {
            hello();
        } else if (Utils.isSimilar(message, "done", "back")) {
            activityBack();
        } else {
            onMessageReceived(message);
        }
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

        return new FirstWord(message.substring(0, index), message.substring(index + 1));
    }

    public static record FirstWord(String word, String message) {}

}
