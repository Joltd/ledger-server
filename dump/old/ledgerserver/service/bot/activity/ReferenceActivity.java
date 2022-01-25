package old.ledgerserver.service.bot.activity;

import old.ledgerserver.util.Tokenizer;
import com.evgenltd.ledgerserver.util.Utils;
import old.ledgerserver.service.bot.BotActivity;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static old.ledgerserver.service.bot.BotState.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReferenceActivity<T extends Reference> extends BotActivity {

    private final BeanFactory beanFactory;

    private Class<T> type;
    private JpaRepository<T, Long> repository;

    public ReferenceActivity(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        command(t -> all(), "all");
        command(this::create, "new", "add");
        command(this::edit, "edit");
        command(this::remove, "remove", "rem", "del", "delete");
    }

    public void setupReference(final String name) {
        type = Utils.classForName("com.evgenltd.ledgerserver.entity." + name);
        repository = beanFactory.getBean(Utils.classForName("com.evgenltd.ledgerserver.repository." + name + "Repository"));
    }

    @Override
    public void hello() {
        super.hello();
        all();
    }

    private void all() {
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

    private void create(final Tokenizer tokenizer) {
        final T reference = Utils.newInstance(type);
        reference.setName(tokenizer.whole());
        repository.save(reference);
        all();
    }

    private void edit(final Tokenizer tokenizer) {
        final String id = tokenizer.next();
        final String name = tokenizer.whole();
        Utils.asLongNoThrow(id)
                .flatMap(repository::findById)
                .ifPresentOrElse(
                        reference -> {
                            reference.setName(name);
                            repository.save(reference);
                            all();
                        },
                        () -> sendMessage("Unable to parse id [%s]", name)
                );
    }

    private void remove(final Tokenizer tokenizer) {
        final String idValue = tokenizer.next();
        Utils.asLongNoThrow(idValue)
                .ifPresentOrElse(
                        id -> {
                            repository.deleteById(id);
                            all();
                        },
                        () -> sendMessage("Unable to parse id [%s]", idValue)
                );
    }

}
