package com.evgenltd.ledgerserver.record;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CodeCardEntry(LocalDateTime date, String comment, String dt, String ct, BigDecimal dtAmount, BigDecimal ctAmount) {}
