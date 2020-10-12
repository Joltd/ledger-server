package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.entity.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.stream.Collectors;

public abstract class AbstractReferenceEditService<R extends Reference> {

    private final BotService botService;
    private final JpaRepository<R, Long> repository;
    private final Class<R> type;

    public AbstractReferenceEditService(
            final BotService botService,
            final JpaRepository<R, Long> repository,
            final Class<R> type
    ) {
        this.botService = botService;
        this.repository = repository;
        this.type = type;
    }

    public void postConstruct() {
        botService.registerHandler("/" + type.getSimpleName().toLowerCase(), this::mainHandler);
    }

    private void mainHandler(final Update update, final Long chatId, final String... args) {
        if (args.length < 2) {
            readCommand(chatId);
            return;
        }

        final String command = args[0].toLowerCase();
        switch (command) {
            case "new":
                newCommand(chatId, args[1]);
                break;
            case "edit":
                if (args.length < 3) {
                    botService.sendMessage(chatId, "Specify id of reference and new name");
                } else {
                    editCommand(chatId, args[1], args[2]);
                }
                break;
            case "delete":
                deleteCommand(chatId, args[1]);
                break;
        }

    }

    private void readCommand(final Long chatId) {
        final String all = repository.findAll()
                .stream()
                .map(reference -> String.format("%s | %s", reference.getId(), reference.getName()))
                .collect(Collectors.joining("\n"));
        if (all.isBlank()) {
            botService.sendMessage(chatId, "No references");
        } else {
            botService.sendMessage(chatId, all);
        }
    }

    private void newCommand(final Long chatId, final String value) {
        final R reference = Utils.newInstance(type);
        reference.setName(value);
        repository.save(reference);
        readCommand(chatId);
    }

    private void editCommand(final Long chatId, final String idValue, final String newValue) {
        Utils.parseLong(idValue)
                .flatMap(repository::findById)
                .ifPresentOrElse(
                        reference -> {
                            reference.setName(newValue);
                            repository.save(reference);
                            readCommand(chatId);
                        },
                        () -> botService.sendMessage(chatId, "Reference with id [%s] not found", idValue)
                );
    }

    private void deleteCommand(final Long chatId, final String idValue) {
        Utils.parseLong(idValue)
                .ifPresentOrElse(
                        id -> {
                            repository.deleteById(id);
                            readCommand(chatId);
                        },
                        () -> botService.sendMessage(chatId, "Reference with id [%s] not found", idValue)
                );
    }

}
