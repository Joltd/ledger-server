package com.evgenltd.ledgerserver.service.bot;

import com.evgenltd.ledgerserver.service.SettingService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SettingsActivity extends BotActivity {

    private final SettingService settingService;

    public SettingsActivity(
            final BotService botService,
            final SettingService settingService
    ) {
        super(botService);
        this.settingService = settingService;
    }

    @Override
    protected void onMessageReceived(final String message) {
        if (message.toLowerCase().contains("all")) {
            hello();
            return;
        }

        final FirstWord wordAndMessage = splitFirstWord(message);
        final String setting = wordAndMessage.word();

        if (!settingService.has(setting)) {
            sendMessage("Unknown setting [%s]", setting);
            return;
        }

        final String value = wordAndMessage.message();
        if (value.isBlank()) {
            sendMessage(settingService.example(setting));
        } else {
            settingService.set(setting, value);
        }

        hello();

    }

    @Override
    protected void hello() {
        sendMessage(String.join("\n", settingService.all()));
    }

}
