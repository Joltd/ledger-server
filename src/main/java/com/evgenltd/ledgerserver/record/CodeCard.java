package com.evgenltd.ledgerserver.record;

import java.util.List;

public record CodeCard(CodeCardEntry before, List<CodeCardEntry> operations, CodeCardEntry turnover, CodeCardEntry after) {}
