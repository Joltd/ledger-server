package com.evgenltd.ledgerserver.platform.browser.record.descriptor;

import java.util.List;

public record MetaField(String reference, FieldType type, List<MetaField> fields) {}
