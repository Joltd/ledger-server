package com.evgenltd.ledgerserver.util;

import com.evgenltd.ledgerserver.ApplicationException;
import com.evgenltd.ledgerserver.service.bot.BotState;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class Utils {

    public static boolean isBlank(final String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isSimilar(final String value, final String... examples) {
        if (isBlank(value)) {
            return false;
        }
        final String cleanedValue = value.trim().toLowerCase();
        return Arrays.stream(examples)
                .anyMatch(example -> Objects.equals(cleanedValue, example.trim().toLowerCase()));
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> classForName(final String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (final ClassNotFoundException e) {
            BotState.sendMessage("Unknown class [%s]", name);
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(final Class<T> type) {
        try {
            final Constructor<T> constructor = type.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            BotState.sendMessage("Unable to instantiate [%s]", type.getName());
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

    public static Optional<LocalDateTime> asDateTime(final String value) {
        return as(value, Utils::toDateTime);
    }

    public static Optional<LocalDateTime> asDateTimeNoThrow(final String value) {
        return asNoThrow(value, Utils::toDateTime);
    }

    public static <T extends Enum<T>> Optional<T> asEnumNoThrow(final String value, final Class<T> type) {
        return Stream.of(type.getEnumConstants())
                .filter(entry -> Utils.isSimilar(entry.name(), value))
                .findFirst();
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
            //
            return Optional.empty();
        }
    }

    private static LocalDateTime toDateTime(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        if (isSimilar(value, "current", "now", "today")) {
            return LocalDateTime.now();
        }

        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String dateTimeToString(final LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}