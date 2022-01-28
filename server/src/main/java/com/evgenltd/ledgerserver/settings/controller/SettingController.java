package com.evgenltd.ledgerserver.settings.controller;

import com.evgenltd.ledgerserver.settings.record.SettingRecord;
import com.evgenltd.ledgerserver.settings.service.SettingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setting")
public class SettingController {

    private final SettingService settingService;

    public SettingController(final SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public SettingRecord load() {
        return settingService.loadRecord();
    }

    @PostMapping
    public void update(@RequestBody final SettingRecord settingRecord) {
        settingService.update(settingRecord);
    }

}
