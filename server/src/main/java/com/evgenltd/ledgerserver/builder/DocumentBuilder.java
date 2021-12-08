package com.evgenltd.ledgerserver.builder;

import com.evgenltd.ledgerserver.document.entity.Document;

import java.time.LocalDateTime;

public class DocumentBuilder {

    public static Document buildDocument(final Document.Type type) {
        final Document document = new Document();
        document.setDate(LocalDateTime.now());
        document.setType(type);
        return document;
    }

}
