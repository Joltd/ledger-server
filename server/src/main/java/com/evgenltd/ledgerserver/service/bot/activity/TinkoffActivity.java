package com.evgenltd.ledgerserver.service.bot.activity;

import com.evgenltd.ledgerserver.service.bot.BotActivity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TinkoffActivity extends BotActivity {

    public TinkoffActivity() {

    }
}
