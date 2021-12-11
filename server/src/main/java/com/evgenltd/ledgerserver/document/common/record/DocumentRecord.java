package com.evgenltd.ledgerserver.document.common.record;

import com.evgenltd.ledgerserver.document.common.entity.Document;

import java.time.LocalDateTime;

public record DocumentRecord(Long id, LocalDateTime date, Document.Type type, String comment) {}