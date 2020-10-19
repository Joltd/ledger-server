package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.entity.Document;
import com.evgenltd.ledgerserver.repository.DocumentRepository;
import com.evgenltd.ledgerserver.repository.JournalEntryRepository;
import com.evgenltd.ledgerserver.service.bot.document.DocumentActivity;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DocumentListActivity extends BotActivity {

    private final DocumentRepository documentRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final BeanFactory beanFactory;

    public DocumentListActivity(
            final BotService botService,
            final DocumentRepository documentRepository,
            final JournalEntryRepository journalEntryRepository,
            final BeanFactory beanFactory
    ) {
        super(botService);
        this.documentRepository = documentRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.beanFactory = beanFactory;
    }

    @Override
    protected void onMessageReceived(final String message) {
        final FirstWord wordAndMessage = splitFirstWord(message);

        if (Utils.isSimilar(wordAndMessage.word(), "new","add")) {
            buildDocumentActivity(wordAndMessage.message(), null)
                    .ifPresentOrElse(
                            this::activityNew,
                            () -> sendMessage("Unknown document type [%s]", wordAndMessage.message())
                    );
        } else if (Utils.isSimilar(wordAndMessage.word(), "edit")) {
            Utils.asLongNoThrow(wordAndMessage.message())
                    .flatMap(documentRepository::findById)
                    .flatMap(document -> buildDocumentActivity(document.getType().name(), document.getId()))
                    .ifPresentOrElse(
                            this::activityNew,
                            () -> sendMessage("Unknown document [%s]", wordAndMessage.message())
                    );
        } else if (Utils.isSimilar(wordAndMessage.word(), "remove", "rem", "delete", "del")) {
            Utils.asLongNoThrow(wordAndMessage.message())
                    .ifPresentOrElse(
                            id -> {
                                journalEntryRepository.deleteByDocumentId(id);
                                documentRepository.deleteById(id);
                                sendMessage("Done");
                                hello();
                            },
                            () -> sendMessage("Unable to parse id [%s]", wordAndMessage.message())
                    );
        } else {
            hello();
        }

    }

    @Override
    protected void hello() {
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

    private String documentToString(final Document document) {
        return String.format("%s | %s | %s", document.getId(), document.getDate(), document.getType());
    }

    public Optional<DocumentActivity> buildDocumentActivity(final String type, final Long documentId) {
        return Stream.of(Document.Type.values())
                .filter(t -> Utils.isSimilar(type, t.name().toLowerCase()))
                .findFirst()
                .map(t -> {
                    final DocumentActivity documentActivity = beanFactory.getBean(t.getActivity());
                    documentActivity.setup(t, documentId);
                    return documentActivity;
                });
    }

}
