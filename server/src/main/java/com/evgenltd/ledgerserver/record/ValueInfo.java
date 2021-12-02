package com.evgenltd.ledgerserver.record;

public interface ValueInfo<T> {
    T get();

    String asString();

    void set(String value);

    void set(T value);

    String print();

    String example();

    void on(Runnable callback);
}
