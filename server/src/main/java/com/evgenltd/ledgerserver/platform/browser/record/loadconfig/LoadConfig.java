package com.evgenltd.ledgerserver.platform.browser.record.loadconfig;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public record LoadConfig(
        PageConfig page,
        SortConfig sort,
        FilterConfig filter
) {
    public PageRequest toPageRequest() {
        return PageRequest.of(
                page.index(),
                page.size(),
                sort != null && sort.reference() != null
                        ? Sort.by(sort.order(), sort.reference())
                        : Sort.unsorted()
        );
    }

    public <T> Specification<T> toSpecification() {
        return Filter.toSpecification(filter().expression());
    }
}
