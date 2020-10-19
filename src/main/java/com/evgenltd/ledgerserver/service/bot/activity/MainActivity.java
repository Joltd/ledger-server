package com.evgenltd.ledgerserver.service.bot.activity;

import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.entity.Reference;
import com.evgenltd.ledgerserver.service.bot.BotActivity;
import com.evgenltd.ledgerserver.service.bot.BotService;
import com.evgenltd.ledgerserver.service.bot.activity.document.DocumentListActivity;
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
        final String command = wordAndMessage.word();

        if (Utils.isSimilar(command, "ref", "reference", "refs")) {
            final ReferenceActivity<Reference> referenceActivity = beanFactory.getBean(ReferenceActivity.class);
            referenceActivity.setupReference(wordAndMessage.message());
            activityNew(referenceActivity);
//        } else if (Utils.isSimilar(command, "")) {
//            final ReferenceSelectionActivity<Reference> referenceSelectionActivity = beanFactory.getBean(ReferenceSelectionActivity.class);
//            referenceSelectionActivity.setupReference(wordAndMessage.message(), result -> {
//                System.out.println(result.getId() + " " + result.getName());
//            });
//            activityNew(referenceSelectionActivity);
        } else if (Utils.isSimilar(command, "settings", "options")) {
            final SettingsActivity settingsActivity = beanFactory.getBean(SettingsActivity.class);
            activityNew(settingsActivity);
        } else if (Utils.isSimilar(command, "documents", "docs")) {
            final DocumentListActivity documentListActivity = beanFactory.getBean(DocumentListActivity.class);
            activityNew(documentListActivity);
        }

    }

    @Override
    protected void hello() {
        sendMessage("ref - All references\nsettings - configuring app settings\ndocs - applied documents");
    }
}
