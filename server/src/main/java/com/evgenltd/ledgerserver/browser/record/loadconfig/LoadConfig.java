package com.evgenltd.ledgerserver.browser.record.loadconfig;

import org.springframework.data.domain.PageRequest;

public record LoadConfig(
        PageRequest pageAndSortConfig,
        FilterConfig filterConfig
) {}
