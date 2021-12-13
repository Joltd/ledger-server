package com.evgenltd.ledgerserver.document.impl;

import com.evgenltd.ledgerserver.document.common.entity.TypeConstant;
import com.evgenltd.ledgerserver.document.common.service.DocumentDefinition;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.FieldType;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.MetaField;
import com.evgenltd.ledgerserver.reference.ReferenceModel;
import com.evgenltd.ledgerserver.reference.entity.Account;
import com.evgenltd.ledgerserver.reference.entity.Currency;
import com.evgenltd.ledgerserver.reference.entity.ExpenseItem;
import com.evgenltd.ledgerserver.util.Utils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component(TypeConstant.BUY_CURRENCY)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BuyCurrencyDefinition extends DocumentDefinition {

    private static final String AMOUNT = "amount";
    private static final String ACCOUNT = "account";
    private static final String CURRENCY = "currency";
    private static final String CURRENCY_RATE = "currencyRate";
    private static final String CURRENCY_AMOUNT = "currencyAmount";
    private static final String COMMISSION = "commission";
    private static final String COMMISSION_AMOUNT = "commissionAmount";

    @Override
    public List<MetaField> meta() {
        return Arrays.asList(
                ReferenceModel.money(AMOUNT),
                ReferenceModel.account(ACCOUNT),
                ReferenceModel.currency(CURRENCY),
                ReferenceModel.money(CURRENCY_RATE),
                ReferenceModel.money(CURRENCY_AMOUNT),
                ReferenceModel.expenseItem(COMMISSION),
                ReferenceModel.money(COMMISSION_AMOUNT)
        );
    }

    @Override
    public void persist() {
        final BigDecimal amount = asMoney(AMOUNT);
        final Long account = asLong(ACCOUNT);
        final Currency currency = asEnum(Currency.class, CURRENCY);
        final BigDecimal currencyRate = asMoney(CURRENCY_RATE);
        final BigDecimal currencyAmount = asMoney(CURRENCY_AMOUNT);
        final Long commission = asLong(COMMISSION);
        final BigDecimal commissionAmount = asMoney(COMMISSION_AMOUNT);

        dt52(amount, account, currency, currencyRate, currencyAmount);
        ct51(amount, account);

        dt91(commissionAmount, account, null, currency, commission);
        ct51(commissionAmount, account);

        comment("Buy %s %s", Utils.formatMoney(currencyAmount), currency.name());
    }

}
