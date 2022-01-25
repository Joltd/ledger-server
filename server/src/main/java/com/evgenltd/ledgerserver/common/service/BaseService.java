package com.evgenltd.ledgerserver.common.service;

import com.evgenltd.ledgerserver.settings.entity.Setting;
import com.evgenltd.ledgerserver.settings.service.SettingService;

public abstract class BaseService {

    private final SettingService settingService;

    public BaseService(final SettingService settingService) {
        this.settingService = settingService;
    }

    protected Setting setting() {
        return settingService.load();
    }

}
