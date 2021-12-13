package com.evgenltd.ledgerserver.reference;

import com.evgenltd.ledgerserver.platform.browser.record.descriptor.FieldType;
import com.evgenltd.ledgerserver.platform.browser.record.descriptor.MetaField;

public class ReferenceModel {

    public static MetaField asLong(final String reference) {
        return MetaField.builder()
                .reference(reference)
                .type(FieldType.NUMBER)
                .build();
    }

    public static MetaField money(final String reference) {
        return MetaField.builder()
                .reference(reference)
                .type(FieldType.NUMBER)
                .build();
    }

    public static MetaField currency(final String reference) {
        return MetaField.builder()
                .reference(reference)
                .type(FieldType.ENUM)
                .api("/reference/currency")
                .build();
    }

    public static MetaField account(final String reference) {
        return MetaField.builder()
                .reference(reference)
                .type(FieldType.OBJECT)
                .api("/reference/account/")
                .build();
    }

    public static MetaField expenseItem(final String reference) {
        return MetaField.builder()
                .reference(reference)
                .type(FieldType.OBJECT)
                .api("/reference/expenseItem/")
                .build();
    }

    public static MetaField incomeItem(final String reference) {
        return MetaField.builder()
                .reference(reference)
                .type(FieldType.OBJECT)
                .api("/reference/incomeItem/")
                .build();
    }

    public static MetaField person(final String reference) {
        return MetaField.builder()
                .reference(reference)
                .type(FieldType.OBJECT)
                .api("/reference/person/")
                .build();
    }

    public static MetaField tickerSymbol(final String reference) {
        return MetaField.builder()
                .reference(reference)
                .type(FieldType.OBJECT)
                .api("/reference/tickerSymbol/")
                .build();
    }

}
