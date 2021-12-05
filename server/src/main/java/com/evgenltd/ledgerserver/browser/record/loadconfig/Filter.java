package com.evgenltd.ledgerserver.browser.record.loadconfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Filter {

    private Filter() {}

    public static <T> Specification<T> toSpecification(final Filter.Expression expression) {
        return new Filter().toSpecificationImpl(expression);
    }

    private <T> Specification<T> toSpecificationImpl(final Filter.Expression expression) {
        return (root, query, cb) -> Filter.this.toPredicate(expression, root, cb);
    }

    private Predicate toPredicate(
            final Filter.Expression expression,
            final Root<?> root,
            final CriteriaBuilder cb
    ) {
        return switch (expression.type()) {
            case STATEMENT -> statementToPredicate(expression, root, cb);
            case NOT -> notToPredicate(expression, root, cb);
            case AND -> andToPredicate(expression, root, cb);
            case OR -> orToPredicate(expression, root, cb);
        };
    }

    private Predicate statementToPredicate(
            final Filter.Expression expression,
            final Root root,
            final CriteriaBuilder cb
    ) {
        final String[] pathEntries = expression.reference().split("\\.");
        Path path = root;
        for (String entry : pathEntries) {
            path = path.get(entry);
        }

        final Comparable value = convert(path, expression.value());
        return switch (expression.operator()) {
            case EQUAL -> cb.equal(path, value);
            case NOT_EQUAL -> cb.notEqual(path, value);
            case LESS -> cb.lessThan(path, value);
            case LESS_EQUAL -> cb.lessThanOrEqualTo(path, value);
            case GREATER -> cb.greaterThan(path, value);
            case GREATER_EQUAL -> cb.greaterThanOrEqualTo(path, value);
            case LIKE -> cb.like(path, (String) value);
            case NOT_LIKE -> cb.notLike(path, (String) value);
            case IS_NULL -> cb.isNull(path);
            case IS_NOT_NULL -> cb.isNotNull(path);
        };
    }

    private Predicate notToPredicate(
            final Filter.Expression expression,
            final Root<?> root,
            final CriteriaBuilder cb
    ) {
        return cb.not(toPredicate(expression.expressions().get(0), root, cb));
    }

    private Predicate andToPredicate(
            final Filter.Expression expression,
            final Root<?> root,
            final CriteriaBuilder cb
    ) {
        return cb.and(
                expression.expressions()
                        .stream()
                        .map(e -> toPredicate(e, root, cb))
                        .collect(Collectors.toList())
                        .toArray(new Predicate[] {})
        );
    }

    private Predicate orToPredicate(
            final Filter.Expression expression,
            final Root<?> root,
            final CriteriaBuilder cb
    ) {
        return cb.or(
                expression.expressions()
                        .stream()
                        .map(e -> toPredicate(e, root, cb))
                        .collect(Collectors.toList())
                        .toArray(new Predicate[] {})
        );
    }

    private Comparable convert(Path path, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        } else if (path.getJavaType().equals(Boolean.class)) {
            return Boolean.valueOf(value);
        } else if (path.getJavaType().equals(LocalDate.class)) {
            return LocalDate.parse(value);
        } else if (path.getJavaType().equals(LocalDateTime.class)) {
            return LocalDateTime.parse(value);
        } else  {
            return value;
        }
    }
    
    public static record Expression(String reference, Operator operator, String value, ExpressionType type, List<Expression> expressions) {}

    public enum ExpressionType {
        STATEMENT,
        NOT,
        AND,
        OR
    }

    public enum Operator {
        EQUAL,
        NOT_EQUAL,
        LESS,
        LESS_EQUAL,
        GREATER,
        GREATER_EQUAL,
        LIKE,
        NOT_LIKE,
        IS_NULL,
        IS_NOT_NULL
    }
    
}
