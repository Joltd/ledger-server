package com.evgenltd.ledgerserver.record;

import com.evgenltd.ledgerserver.entity.Setting;

public interface SettingInfo<T> {
    String name();

    T get(Setting setting);

    void set(Setting setting, String value);

    String print(Setting setting);

    String example();
}
