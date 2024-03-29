package old.ledgerserver.service.bot.activity;

import com.evgenltd.ledgerserver.util.Utils;
import old.ledgerserver.service.bot.BotActivity;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static old.ledgerserver.service.bot.BotState.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReferenceSelectionActivity<T extends Reference> extends BotActivity {

    private final BeanFactory beanFactory;

    private Class<T> type;
    private JpaRepository<T, Long> repository;
    private Consumer<T> callback;
    private List<T> data;

    public ReferenceSelectionActivity(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setupReference(final String name, final Consumer<T> callback) {
        type = Utils.classForName("com.evgenltd.ledgerserver.entity." + name);
        repository = beanFactory.getBean(Utils.classForName("com.evgenltd.ledgerserver.repository." + name + "Repository"));
        this.callback = callback;
        data = repository.findAll();
    }

    @Override
    protected void onMessageReceived(final String message) {
        if (message.toLowerCase().contains("all")) {
            read();
            return;
        }
        data.stream()
                .filter(entry -> entry.getId().toString().equalsIgnoreCase(message) || entry.getName().toLowerCase().contains(message.toLowerCase()))
                .findFirst()
                .ifPresentOrElse(
                        entry -> {
                            callback.accept(entry);
                            activityBack();
                        },
                        () -> sendMessage("Not found")
                );
    }

    private void read() {
        final String all = repository.findAll()
                .stream()
                .map(reference -> String.format("%s | %s", reference.getId(), reference.getName()))
                .collect(Collectors.joining("\n"));
        if (all.isBlank()) {
            sendMessage("No references");
        } else {
            sendMessage(all);
        }
    }

}
