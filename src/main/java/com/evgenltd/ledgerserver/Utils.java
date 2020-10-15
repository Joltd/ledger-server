package com.evgenltd.ledgerserver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;

public class Utils {

    public static <T> Class<T> classForName(final String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(final Class<T> type) {
        try {
            final Constructor<T> constructor = type.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Long> asLong(final String value) {
        return as(value, Long::parseLong);
    }

    public static Optional<Long> asLongNoThrow(final String value) {
        return asNoThrow(value, Long::parseLong);
    }

    public static Optional<Integer> asInt(final String value) {
        return as(value, Integer::parseInt);
    }

    public static Optional<Integer> asIntNoThrow(final String value) {
        return asNoThrow(value, Integer::parseInt);
    }

    public static Optional<Boolean> asBoolean(final String value) {
        return as(value, Boolean::parseBoolean);
    }

    public static Optional<BigDecimal> asBigDecimal(final String value) {
        return as(value, BigDecimal::new);
    }

    private static <T> Optional<T> as(final String value, final Function<String, T> converter) {
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(converter.apply(value));
        } catch (Exception e) {
            throw new ApplicationException("Unable to parse [%s]", value);
        }
    }

    private static <T> Optional<T> asNoThrow(final String value, final Function<String, T> converter) {
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(converter.apply(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
