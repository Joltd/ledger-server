package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.util.Tokenizer;
import com.evgenltd.ledgerserver.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.evgenltd.ledgerserver.service.bot.BotState.*;

public abstract class BotActivity {

    private final List<Command> commands = new ArrayList<>();

    public BotActivity() {
        command(message -> done(), "done", "back");
        command(message -> help(), "help", "man");
    }

    void messageReceived(final String message) {
        final Tokenizer tokenizer = Tokenizer.of(message);
        final String token = tokenizer.next();
        for (final Command command : commands) {
            if (Utils.isSimilar(token, command.tokens())) {
                command.handler().accept(tokenizer);
                return;
            }
        }

        onMessageReceived(message);
    }

    protected void onMessageReceived(final String message) {}

    public void done() {
        activityBack();
    }

    public void help() {
        final String help = commands.stream()
                .map(command -> "- " + String.join(",", command.tokens()))
                .collect(Collectors.joining("\n"));
        sendMessage(help);
    }

    protected void command(final Consumer<Tokenizer> command, final String... tokens) {
        commands.add(new Command(tokens, command));
    }

    private static record Command(String[] tokens, Consumer<Tokenizer> handler) {}

}
