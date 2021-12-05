package com.evgenltd.ledgerserver.browser.record.loadconfig;

import org.springframework.data.domain.Sort;

public record SortConfig(String reference, Sort.Direction order) {
}
