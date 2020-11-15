package com.evgenltd.ledgerserver.builder;

import com.evgenltd.ledgerserver.util.Utils;
import com.evgenltd.ledgerserver.entity.Reference;
import com.evgenltd.ledgerserver.record.ValueInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ValueInfoBuilder {

    public static  <T> ValueInfo<T> primitiveValue(
            final Function<String, Optional<T>> fromString,
            final Function<T, String> toString,
            final String example
    ) {
        return new ValueInfoImpl<>(
                fromString,
                toString,
                toString,
                () -> example
        );
    }

    public static <T extends Reference> ValueInfo<T> referenceValue(final JpaRepository<T, Long> repository) {
        return new ValueInfoImpl<>(
                value -> Utils.asLongNoThrow(value)
                        .flatMap(repository::findById),
                value -> value.getId().toString(),
                Reference::asString,
                () -> repository.findAll()
                        .stream()
                        .map(Reference::asString)
                        .collect(Collectors.joining("\n"))
        );
    }

    private static final class ValueInfoImpl<T> implements ValueInfo<T> {

        private T value;
        private final Function<String,Optional<T>> fromString;
        private final Function<T,String> toString;
        private final Function<T,String> print;
        private final Supplier<String> example;
        private final List<Runnable> callbacks = new ArrayList<>();

        public ValueInfoImpl(
                final Function<String, Optional<T>> fromString,
                final Function<T,String> toString,
                final Function<T,String> print,
                final Supplier<String> example
        ) {
            this.fromString = fromString;
            this.toString = toString;
            this.print = print;
            this.example = example;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public String asString() {
            return value != null ? toString.apply(value) : "";
        }

        @Override
        public void set(final String value) {
            fromString.apply(value)
                    .ifPresentOrElse(
                            result -> this.value = result,
                            () -> this.value = null
                    );
            callbacks.forEach(Runnable::run);
        }

        @Override
        public void set(final T value) {
            this.value = value;
            callbacks.forEach(Runnable::run);
        }

        @Override
        public String print() {
            return value != null ? print.apply(value) : "";
        }

        @Override
        public String example() {
            return example.get();
        }

        @Override
        public void on(final Runnable callback) {
            callbacks.add(callback);
        }
    }
}
