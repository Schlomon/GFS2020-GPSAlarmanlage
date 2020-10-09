package com.example.smsreciever;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class SMSForwarderCommon {
    public static boolean serviceIsRunning = false;

    public static Context defaultContext;

    public static HashMap<Setting, String> settings;

    enum Setting {
        senderPhoneNumber(SMSForwarderCommon.defaultContext.getString(R.string.phone_number_id)),
        targetURL(SMSForwarderCommon.defaultContext.getString(R.string.server_URL_id)),
        port(SMSForwarderCommon.defaultContext.getString(R.string.port_id));

        private String settingID;

        Setting(String settingID) {
            this.settingID = settingID;
        }

        public String getSettingID() {
            return settingID;
        }

        public static final Map<String, Setting> lookup = new HashMap<>();

        static {
            for (Setting setting : Setting.values()) {
                lookup.put(setting.settingID, setting);
            }
        }

        public static Setting getSetting(String settingID) {
            return lookup.get(settingID);
        }
    }

    public static void refreshSettings() {
        settings = FileHandler.readSettingsFromFile();
    }
}
