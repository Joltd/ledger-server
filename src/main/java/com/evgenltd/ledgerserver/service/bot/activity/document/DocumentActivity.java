package com.evgenltd.ledgerserver.service.bot.activity.document;

import com.evgenltd.ledgerserver.service.DocumentComponent;
import com.evgenltd.ledgerserver.state.DocumentState;
import com.evgenltd.ledgerserver.util.Tokenizer;
import com.evgenltd.ledgerserver.entity.*;
import com.evgenltd.ledgerserver.record.ValueInfo;
import com.evgenltd.ledgerserver.service.bot.BotState;
import com.evgenltd.ledgerserver.service.bot.BotActivity;
import org.springframework.beans.factory.BeanFactory;

import java.util.*;

public abstract class DocumentActivity extends BotActivity {

    private static final String DATE = "date";
    
    private final BeanFactory beanFactory;
    private DocumentComponent documentComponent;

    public DocumentActivity(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;

        documentComponent.dateField(DATE);

        command(this::update, "&");
        command(tokenizer -> save(), "save", "apple");
        command(tokenizer -> cancel(), "discard", "cancel", "close", "back");
    }

    public void setup(final Document document) {
        documentComponent = beanFactory.getBean(DocumentComponent.class);
        documentComponent.setup(document);
        DocumentState.set(documentComponent);

        documentComponent.set(DATE, document.getDate());
        if (document.getId() == null) {
            onDefaults();
        }
    }

    protected void onDefaults() {}

    @Override
    public void done() {
        DocumentState.reset();
        super.done();
    }

    private void update(final Tokenizer tokenizer) {
        documentComponent.setContent(tokenizer.whole());
    }

    private void save() {
        onSave();
        documentComponent.save();
        cancel();
    }

    protected abstract void onSave();

    private void cancel() {
        DocumentState.reset();
        BotState.activityBack();
    }

    @Override
    protected void onMessageReceived(final String message) {
        final Tokenizer tokenizer = Tokenizer.of(message);
        final String field = tokenizer.next();
        final Optional<ValueInfo<Object>> valueInfo = documentComponent.getInfo(field);
        if (valueInfo.isPresent()) {
            final String value = tokenizer.next();
            if (value.isBlank()) {
                BotState.sendMessage(valueInfo.get().example());
            } else {
                valueInfo.get().set(value);
            }
        }
    }

}
