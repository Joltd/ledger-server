package old.ledgerserver.service.bot.activity.document;

import old.ledgerserver.util.Tokenizer;
import com.evgenltd.ledgerserver.util.Utils;
import old.ledgerserver.document.common.entity.Document;
import com.evgenltd.ledgerserver.common.repository.JournalEntryRepository;
import old.ledgerserver.service.bot.BotActivity;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static old.ledgerserver.service.bot.BotState.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DocumentListActivity extends BotActivity {

    private final DocumentRepository documentRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final BeanFactory beanFactory;

    public DocumentListActivity(
            final DocumentRepository documentRepository,
            final JournalEntryRepository journalEntryRepository,
            final BeanFactory beanFactory
    ) {
        this.documentRepository = documentRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.beanFactory = beanFactory;

        command(this::create, "new", "add");
        command(this::edit, "edit");
        command(this::remove, "remove", "rem", "delete", "del");
        command(tokenizer -> all(), "all");
        command(tokenizer -> butchUpdate(), "batchUpdate", "bu");
    }

    @Override
    public void hello() {
        super.hello();
        all();
    }

    private void create(final Tokenizer tokenizer) {
        final String typeToken = tokenizer.next();
        Stream.of(Document.Type.values())
                .filter(type -> Utils.isSimilar(typeToken, type.name()))
                .findFirst()
                .ifPresentOrElse(
                        type -> {
                            final Document document = new Document();
                            document.setType(type);
                            document.setDate(LocalDateTime.now());

                            final DocumentActivity documentActivity = beanFactory.getBean(type.getActivity());
                            documentActivity.setup(document);
                            activityNew(documentActivity);
                        },
                        () -> {
                            final String allowedDocumentTypes = Stream.of(Document.Type.values())
                                    .map(Enum::name)
                                    .collect(Collectors.joining("\n"));
                            sendMessage("Allowed types: \n" + allowedDocumentTypes);
                        }
                );

    }

    private void edit(final Tokenizer tokenizer) {
        final String idToken = tokenizer.next();
        Utils.asLongNoThrow(idToken)
                .flatMap(documentRepository::findById)
                .ifPresentOrElse(
                        document -> {
                            final DocumentActivity documentActivity = beanFactory.getBean(document.getType().getActivity());
                            documentActivity.setup(document);
                            activityNew(documentActivity);
                        },
                        () -> sendMessage("Document [%s] not found", idToken)
                );
    }

    private void remove(final Tokenizer tokenizer) {
        final String idToken = tokenizer.next();
        Utils.asLongNoThrow(idToken)
                .ifPresentOrElse(
                        id -> {
                            journalEntryRepository.deleteByDocumentId(id);
                            documentRepository.deleteById(id);
                            all();
                        },
                        () -> sendMessage("Unable to parse id [%s]", idToken)
                );
    }

    private void all() {
        final String all = documentRepository.findAll()
                .stream()
                .map(this::documentToString)
                .collect(Collectors.joining("\n"));
        if (all.isBlank()) {
            sendMessage("No documents");
        } else {
            sendMessage(all);
        }
    }

    private void butchUpdate() {
        documentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Document::getDate))
                .forEach(document -> {
                    final DocumentActivity activity = beanFactory.getBean(document.getType().getActivity());
                    activity.setup(document);
                    activity.apply();
                });
    }

    private String documentToString(final Document document) {
        return String.format("%s | %s | %s | %s", document.getId(), document.getDate(), document.getType(), document.getComment());
    }

}
