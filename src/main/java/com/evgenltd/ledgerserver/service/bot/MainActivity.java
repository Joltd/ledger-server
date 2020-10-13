package com.evgenltd.ledgerserver.service.bot;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MainActivity extends BotActivity {

    private final BeanFactory beanFactory;

    public MainActivity(
            final BotService botService,
            final BeanFactory beanFactory
    ) {
        super(botService);
        this.beanFactory = beanFactory;
    }

    @Override
    public void onMessageReceived(final String message) {

        final FirstWord wordAndMessage = splitFirstWord(message);

        if (wordAndMessage.word().toLowerCase().contains("ref")) {
            final ReferenceActivity referenceActivity = beanFactory.getBean(ReferenceActivity.class);
            referenceActivity.setupReference(wordAndMessage.message());
            activityNew(referenceActivity);
        }
    }

}
