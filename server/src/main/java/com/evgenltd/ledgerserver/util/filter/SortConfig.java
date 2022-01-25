package com.evgenltd.ledgerserver.util.filter;

import org.springframework.data.domain.Sort;

public record SortConfig(String reference, Sort.Direction order) {}