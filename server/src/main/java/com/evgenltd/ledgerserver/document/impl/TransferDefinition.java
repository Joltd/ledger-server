package com.evgenltd.ledgerserver.document.impl;

import com.evgenltd.ledgerserver.document.common.entity.TypeConstant;
import com.evgenltd.ledgerserver.document.common.service.DocumentDefinition;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.FieldType;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.MetaField;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.MetaModel;
import com.evgenltd.ledgerserver.reference.ReferenceModel;
import com.evgenltd.ledgerserver.platform.entities.reference.account.Account;
import com.evgenltd.ledgerserver.reference.repository.AccountRepository;
import com.evgenltd.ledgerserver.util.Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component(value = TypeConstant.TRANSFER)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransferDefinition extends DocumentDefinition {

    private static final String AMOUNT = "amount";
    private static final String FROM = "from";
    private static final String TO = "to";

    private final AccountRepository accountRepository;

    public TransferDefinition(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<MetaField> meta() {
        return Arrays.asList(
                ReferenceModel.money(AMOUNT),
                ReferenceModel.account(FROM),
                ReferenceModel.account(TO)
        );
    }

    @Override
    public void persist() {
        final BigDecimal amount = asMoney(AMOUNT);
        final Long fromId = asLong(FROM);
        final Long toId = asLong(TO);

        dt51(amount, toId);
        ct51(amount, fromId);

        final Account from = accountRepository.getById(fromId);
        final Account to = accountRepository.getById(toId);

        comment("Move %s from '%s' to '%s'", Utils.formatMoney(amount), from.getName(), to.getName());
    }

}
