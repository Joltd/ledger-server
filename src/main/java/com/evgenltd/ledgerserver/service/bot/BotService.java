package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.service.bot.activity.MainActivity;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.*;

@Service
public class BotService extends TelegramLongPollingBot {

    @Value("${bot.token}")
    private String botToken;

    private final BeanFactory beanFactory;

    private final Map<Long, ActivityStack> activities = new HashMap<>();

    public BotService(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return "LedgerBot";
    }

    @Override
    public void onUpdateReceived(final Update update) {
        if (update.getMessage() == null) {
            return;
        }

        final Long chatId = update.getMessage().getChatId();
        final String text = update.getMessage().getText();

        final ActivityStack activityStack = getActivityStack(chatId);
        activityStack.current().messageReceived(text);
    }

    private ActivityStack getActivityStack(final Long chatId) {
        return activities.computeIfAbsent(chatId, id -> {
            final MainActivity activity = beanFactory.getBean(MainActivity.class);
            activity.setChatId(chatId);

            final ActivityStack newActivityStack = new ActivityStack();
            newActivityStack.push(activity);
            return newActivityStack;
        });
    }

    public void activityNew(final Long chatId, final BotActivity activity) {
        activity.setChatId(chatId);
        getActivityStack(chatId).push(activity);
        activity.hello();
    }

    public void activityBack(final Long chatId) {
        getActivityStack(chatId).back();
        getActivityStack(chatId).current().hello();
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

    private static final class ActivityStack {
        private final Deque<BotActivity> stack = new LinkedList<>();

        public BotActivity current() {
            return stack.peek();
        }

        public void push(final BotActivity activity) {
            stack.push(activity);
        }

        public void back() {
            if (stack.size() <= 1) {
                return;
            }

            stack.pop();
        }

    }

}
