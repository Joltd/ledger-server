package com.evgenltd.ledgerserver.service.bot.activity.doimport;

import com.evgenltd.ledgerserver.service.bot.BotActivity;
import com.evgenltd.ledgerserver.service.bot.BotState;
import com.evgenltd.ledgerserver.util.Tokenizer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ImportListActivity extends BotActivity {

    private final BeanFactory beanFactory;

    public ImportListActivity(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;

        command(this::tinkoffStonks, "tinkoff stonks", "stonks", "tin");
    }

    private void tinkoffStonks(final Tokenizer tokenizer) {
        final TinkoffStonksImportActivity tinkoffStonksImportActivity = beanFactory.getBean(TinkoffStonksImportActivity.class);
        BotState.activityNew(tinkoffStonksImportActivity);
    }

}
