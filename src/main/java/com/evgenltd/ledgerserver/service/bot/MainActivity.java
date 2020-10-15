package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.entity.Reference;
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
        final String command = wordAndMessage.word().toLowerCase();

        if (command.contains("ref")) {
            final ReferenceActivity<Reference> referenceActivity = beanFactory.getBean(ReferenceActivity.class);
            referenceActivity.setupReference(wordAndMessage.message());
            activityNew(referenceActivity);
        } else if (command.contains("select")) {
            final ReferenceSelectionActivity<Reference> referenceSelectionActivity = beanFactory.getBean(ReferenceSelectionActivity.class);
            referenceSelectionActivity.setupReference(wordAndMessage.message(), result -> {
                System.out.println(result.getId() + " " + result.getName());
            });
            activityNew(referenceSelectionActivity);
        } else if (command.contains("settings")) {
            final SettingsActivity settingsActivity = beanFactory.getBean(SettingsActivity.class);
            activityNew(settingsActivity);
        }
    }

}
