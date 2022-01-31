package com.evgenltd.ledgerserver.base.record;

import java.util.List;

public record TurnoverRecord(
        List<TurnoverEntryRecord> entries,
        TurnoverEntryRecord total
) {}
