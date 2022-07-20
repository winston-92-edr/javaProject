package com.mynet.socialserver.request;

import com.mynet.shared.types.SettingsTypes;

public class UpdateSettingsRequest {
    private SettingsTypes settingType;
    private boolean status;

    public SettingsTypes getSettingType() {
        return settingType;
    }

    public boolean isStatus() {
        return status;
    }
}
