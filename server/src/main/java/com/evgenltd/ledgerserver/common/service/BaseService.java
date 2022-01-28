package com.evgenltd.ledgerserver.common.service;

import com.evgenltd.ledgerserver.settings.entity.Setting;
import com.evgenltd.ledgerserver.settings.service.SettingService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

public abstract class BaseService {

    private final SettingService settingService;

    public BaseService(final SettingService settingService) {
        this.settingService = settingService;
    }

    protected Setting setting() {
        return settingService.load();
    }

    protected String formatMoney(final BigDecimal money) {
        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(Objects.requireNonNullElse(money, BigDecimal.ZERO));
    }

}
