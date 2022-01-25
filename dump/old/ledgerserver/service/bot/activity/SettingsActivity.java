package old.ledgerserver.service.bot.activity;

import old.ledgerserver.service.SettingService;
import old.ledgerserver.service.bot.BotActivity;
import old.ledgerserver.util.Tokenizer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static old.ledgerserver.service.bot.BotState.sendMessage;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SettingsActivity extends BotActivity {

    private final SettingService settingService;

    public SettingsActivity(final SettingService settingService) {
        this.settingService = settingService;

        command(tokenizer -> all(), "all");
    }

    @Override
    public void hello() {
        super.hello();
        all();
    }

    private void all() {
        sendMessage(String.join("\n", settingService.all()));
    }

    @Override
    protected void onMessageReceived(final String message) {
        final Tokenizer tokenizer = Tokenizer.of(message);
        final String setting = tokenizer.next();

        if (!settingService.has(setting)) {
            sendMessage("Unknown setting [%s]", setting);
            return;
        }

        final String value = tokenizer.next();
        if (value.isBlank()) {
            sendMessage(settingService.example(setting));
        } else {
            settingService.set(setting, value);
        }

        all();

    }

}
