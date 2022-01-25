package old.ledgerserver.service.bot;

import old.ledgerserver.service.bot.activity.MainActivity;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

//@Service
public class BotService extends TelegramLongPollingBot {

    @Value("${bot.token}")
    private String botToken;

    private final BeanFactory beanFactory;

    private final Map<Long, ActivityStack> activities = new HashMap<>();

    public BotService(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    public void postConstruct() {
        BotState.setup(this);
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
        BotState.setup(chatId);

        final String text = update.getMessage().getText();
        getActivityStack(chatId).current().messageReceived(text);

        BotState.reset();
    }

    public void sendMessage(final Long chatId, final String message, final Object... args) {
        execute(new SendMessage(chatId.toString(), String.format(message, args)));
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(final Method method)  {
        try {
            return super.execute(method);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void activityNew(final Long chatId, final BotActivity activity) {
        final ActivityStack activityStack = getActivityStack(chatId);
        activityStack.push(activity);
        activity.hello();
    }

    public void activityBack(final Long chatId) {
        final ActivityStack activityStack = getActivityStack(chatId);
        activityStack.back();
        activityStack.current().hello();
    }

    private ActivityStack getActivityStack(final Long chatId) {
        return activities.computeIfAbsent(chatId, id -> {
            final ActivityStack activityStack = new ActivityStack();
            activityStack.push(beanFactory.getBean(MainActivity.class));
            return activityStack;
        });
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
