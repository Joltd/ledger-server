package com.evgenltd.ledgerserver.document.common.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.UUID;

public class JournalOperationBuilder {

    private final Document document;
    private final ObjectNode definition;
    private JournalEntry dt;
    private JournalEntry ct;
    private UUID uuid = UUID.randomUUID();
    private JournalEntry context;

    private JournalOperationBuilder(final Document document, final ObjectNode definition) {
        this.document = document;
        this.definition = definition;
    }

    public static JournalOperationBuilder of(final Document document, final ObjectNode definition) {
        return new JournalOperationBuilder(document, definition);
    }

    public JournalOperationBuilder dt(final JournalEntry dt) {
        this.dt = dt;
        this.dt.setType(JournalEntry.Type.DEBIT);
        fillJournalEntry(this.dt);
        return this;
    }

    public JournalOperationBuilder ct(final JournalEntry ct) {
        this.ct = ct;
        this.ct.setType(JournalEntry.Type.CREDIT);
        fillJournalEntry(this.ct);
        return this;
    }

    private void fillJournalEntry(final JournalEntry entry) {
        entry.setDate(document.getDate());
        entry.setOperation(uuid.toString());
        entry.setDocument(document);
    }

}
