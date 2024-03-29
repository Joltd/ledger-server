package old.ledgerserver.service.bot.activity.document;

import old.ledgerserver.document.common.entity.Document;
import old.ledgerserver.service.bot.DocumentComponent;
import old.ledgerserver.util.Tokenizer;
import old.ledgerserver.record.ValueInfo;
import old.ledgerserver.service.bot.BotState;
import old.ledgerserver.service.bot.BotActivity;
import org.springframework.beans.factory.BeanFactory;

import java.util.*;

public abstract class DocumentActivity extends BotActivity {

    public static final String DATE = "date";
    public static final String COMMENT = "comment";

    private final DocumentComponent documentComponent;

    public DocumentActivity(final BeanFactory beanFactory) {
        documentComponent = beanFactory.getBean(DocumentComponent.class);

        documentComponent.dateField(DATE);
        documentComponent.stringField(COMMENT);

        command(this::update, "&");
        command(tokenizer -> save(), "save", "apply");
        command(tokenizer -> BotState.activityBack(), "discard", "cancel", "close", "back");
    }

    public DocumentComponent document() {
        return documentComponent;
    }

    public void setup(final Document document) {
        documentComponent.setup(document);
        documentComponent.set(DATE, document.getDate());
        if (document.getId() == null) {
            onDefaults();
        }
    }

    protected void onDefaults() {}

    @Override
    public void done() {
        super.done();
    }

    private void update(final Tokenizer tokenizer) {
        documentComponent.setContent(tokenizer.whole());
        hello();
    }

    public void apply() {
        onSave();
        documentComponent.save();
    }

    private void save() {
        apply();
        BotState.activityBack();
    }

    protected abstract void onSave();

    @Override
    public void hello() {
        super.hello();
        BotState.sendMessage(documentComponent.print());
    }

    @Override
    protected void onMessageReceived(final String message) {
        final Tokenizer tokenizer = Tokenizer.of(message);
        final String field = tokenizer.next();
        final Optional<ValueInfo<Object>> valueInfo = documentComponent.getInfo(field);
        if (valueInfo.isPresent()) {
            final String value = tokenizer.whole();
            if (value.isBlank()) {
                BotState.sendMessage(valueInfo.get().example());
            } else {
                valueInfo.get().set(value);
                hello();
            }
        }
    }

}
