package com.evgenltd.ledgerserver.document.impl;

import com.evgenltd.ledgerserver.document.common.entity.TypeConstant;
import com.evgenltd.ledgerserver.document.common.service.DocumentDefinition;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.FieldType;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.MetaField;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.MetaModel;
import com.evgenltd.ledgerserver.reference.ReferenceModel;
import com.evgenltd.ledgerserver.util.Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component(TypeConstant.FOUNDER_CONTRIBUTION)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FounderContributionDefinition extends DocumentDefinition {

    private final static String AMOUNT = "amount";
    private final static String ACCOUNT = "account";

    @Override
    public List<MetaField> meta() {
        return Arrays.asList(
                ReferenceModel.money(AMOUNT),
                ReferenceModel.account(ACCOUNT)
        );
    }

    @Override
    public void persist() {

        final BigDecimal amount = asMoney(AMOUNT);
        final Long account = asLong(ACCOUNT);

        dt75(amount);
        ct80(amount);

        dt51(amount, account);
        ct75(amount);

        comment("Contribution %s", Utils.formatMoney(amount));

    }


}
