package old.ledgerserver.service.bot.activity;

import old.ledgerserver.util.Tokenizer;
import old.ledgerserver.service.bot.BotActivity;
import old.ledgerserver.service.bot.activity.document.DocumentListActivity;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static old.ledgerserver.service.bot.BotState.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MainActivity extends BotActivity {

    private final BeanFactory beanFactory;

    public MainActivity(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        command(this::reference, "ref", "reference", "refs");
        command(this::settings, "settings", "options");
        command(this::documents, "documents", "docs");
    }

    private void reference(final Tokenizer tokenizer) {
//        final List<Class<? extends Reference>> refs = Arrays.asList(
//                Account.class,
//                ExpenseItem.class,
//                IncomeItem.class,
//                Person.class,
//                TickerSymbol.class
//        );
//
//        final String type = tokenizer.next();
//        refs.stream()
//                .filter(referenceType -> Utils.isSimilar(type, referenceType.getSimpleName()))
//                .findFirst()
//                .ifPresentOrElse(
//                        reference -> {
//                            final ReferenceActivity<Reference> referenceActivity = beanFactory.getBean(ReferenceActivity.class);
//                            referenceActivity.setupReference(reference.getSimpleName());
//                            activityNew(referenceActivity);
//                        },
//                        () -> {
//                            final String allowedReferenceTypes = refs.stream()
//                                    .map(c -> "- " + c.getSimpleName())
//                                    .collect(Collectors.joining("\n"));
//                            sendMessage("Allowed types: \n" + allowedReferenceTypes);
//                        }
//                );
    }

    private void settings(final Tokenizer tokenizer) {
        final SettingsActivity settingsActivity = beanFactory.getBean(SettingsActivity.class);
        activityNew(settingsActivity);
    }

    private void documents(final Tokenizer tokenizer) {
        final DocumentListActivity documentListActivity = beanFactory.getBean(DocumentListActivity.class);
        activityNew(documentListActivity);
    }

}
