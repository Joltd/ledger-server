package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.entity.Reference;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReferenceActivity<T extends Reference> extends BotActivity {

    private final BeanFactory beanFactory;

    private Class<T> type;
    private JpaRepository<T, Long> repository;

    public ReferenceActivity(
            final BotService botService,
            final BeanFactory beanFactory
    ) {
        super(botService);
        this.beanFactory = beanFactory;
    }

    public void setupReference(final String name) {
        type = Utils.classForName("com.evgenltd.ledgerserver.entity." + name);
        repository = beanFactory.getBean(Utils.classForName("com.evgenltd.ledgerserver.repository." + name + "Repository"));
    }

    @Override
    public void onMessageReceived(final String message) {
        final FirstWord wordAndMessage = splitFirstWord(message);
        switch (wordAndMessage.word()) {
            case "new": {
                final T reference = Utils.newInstance(type);
                reference.setName(wordAndMessage.message());
                repository.save(reference);
                read();
                return;
            }
            case "edit": {
                final FirstWord idAndName = splitFirstWord(wordAndMessage.message());
                parseLong(idAndName.word()).flatMap(id -> repository.findById(id))
                        .ifPresent(reference -> {
                            reference.setName(idAndName.message());
                            repository.save(reference);
                            read();
                        });
                return;
            }
            case "remove": {
                parseLong(wordAndMessage.message()).ifPresent(id -> {
                    repository.deleteById(id);
                    read();
                });
                return;
            }
            default: {
                read();
            }
        }
    }

    @Override
    protected void hello() {
        read();
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

    private Optional<Long> parseLong(final String value) {
        try {
            return Optional.of(Long.parseLong(value));
        } catch (final NumberFormatException e) {
            sendMessage("Unable to parse [%s]", value);
            return Optional.empty();
        }
    }

}
