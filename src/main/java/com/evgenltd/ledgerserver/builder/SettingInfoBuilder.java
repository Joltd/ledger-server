package com.evgenltd.ledgerserver.builder;

import com.evgenltd.ledgerserver.Utils;
import com.evgenltd.ledgerserver.entity.Reference;
import com.evgenltd.ledgerserver.entity.Setting;
import com.evgenltd.ledgerserver.record.SettingInfo;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingInfoBuilder {

    public static <T> SettingInfo<T> primitiveSetting(final String name, final Function<String, Optional<T>> converter, final String example) {
        return new SettingInfoImpl<>(
                name,
                converter,
                value -> converter.apply(value).map(Object::toString),
                () -> example
        );
    }

    public static <T extends Reference> SettingInfo<T> referenceSetting(final String name, final JpaRepository<T, Long> repository) {
        return new SettingInfoImpl<>(
                name,
                value -> Utils.asLongNoThrow(value).flatMap(repository::findById),
                value -> Utils.asLongNoThrow(value).flatMap(repository::findById).map(Reference::asString),
                () -> repository.findAll()
                        .stream()
                        .map(Reference::asString)
                        .collect(Collectors.joining("\n"))
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> SettingInfo<T> beanSetting(
            final String name,
            final BeanFactory beanFactory,
            final Class<?>... beans
    ) {
        return new SettingInfoImpl<>(
                name,
                value -> Optional.of((T) beanFactory.getBean(value)),
                Optional::ofNullable,
                () -> Stream.of(beans).map(Class::getSimpleName).collect(Collectors.joining("\n"))
        );
    }

    private static final class SettingInfoImpl<T> implements SettingInfo<T> {
        private final String name;
        private final Function<String, Optional<T>> fromString;
        private final Function<String, Optional<String>> toString;
        private final Supplier<String> example;

        public SettingInfoImpl(
                final String name,
                final Function<String, Optional<T>> fromString,
                final Function<String, Optional<String>> toString,
                final Supplier<String> example
        ) {
            this.name = name;
            this.fromString = fromString;
            this.toString = toString;
            this.example = example;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public T get(final Setting setting) {
            return fromString.apply(setting.getValue()).orElse(null);
        }

        @Override
        public void set(final Setting setting, final String value) {
            fromString.apply(value)
                    .ifPresentOrElse(
                            result -> setting.setValue(value),
                            () -> setting.setValue(null)
                    );
        }

        @Override
        public String print(final Setting setting) {
            return toString.apply(setting.getValue()).orElse("");
        }

        @Override
        public String example() {
            return example.get();
        }
    }

}
