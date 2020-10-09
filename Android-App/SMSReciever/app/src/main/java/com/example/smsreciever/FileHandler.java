package com.example.smsreciever;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

class FileHandler {

    public enum File {
        lastSMS(SMSForwarderCommon.defaultContext.getString(R.string.SMS_save_file)),
        settings(SMSForwarderCommon.defaultContext.getString(R.string.settings_save_file));

        private String path;

        File(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    // Read/Write methods for SMS only
    static void writeSMSToFile(String sender, String body) {
        String saveLine = "There was an error in FileHandler.java while parsing data to JSON format";
        try {
            JSONObject data = new JSONObject(body);
            data.put("sender", sender);
            saveLine = data.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FileHandler.writeToFile(saveLine, File.lastSMS);
    }

    static String readLastSMS() {
        return FileHandler.readFromFile(File.lastSMS);
    }

    // Read/Write methods for settings only
    static void writeSettingsToFile(HashMap<SMSForwarderCommon.Setting, String> settings) {
        String allSettingsString = "";
        for (Map.Entry<SMSForwarderCommon.Setting, String> entry: settings.entrySet()) {
            allSettingsString = allSettingsString + "\n" + entry.getKey().getSettingID() + "~" + entry.getValue();
        }
        FileHandler.writeToFile(allSettingsString, File.settings);
        SMSForwarderCommon.refreshSettings();
    }

    static HashMap<SMSForwarderCommon.Setting, String> readSettingsFromFile() {
        HashMap<SMSForwarderCommon.Setting, String> allSettingsHash= new HashMap<>();
        String allSettingsString = FileHandler.readFromFile(File.settings);

        String[] allSettingsArray = allSettingsString.split("\n");

        for (String setting : allSettingsArray) {
            String[] singleSplitSetting = setting.split("~");
            if (!setting.equals("") && singleSplitSetting.length > 1) {
                allSettingsHash.put(SMSForwarderCommon.Setting.getSetting(singleSplitSetting[0]), singleSplitSetting[1]);
            }
        }

        return allSettingsHash;
    }

    // General Read/Write methods
    private static void writeToFile(String content, File file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(SMSForwarderCommon.defaultContext.openFileOutput(file.getPath(), Context.MODE_PRIVATE));
            outputStreamWriter.write(content);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("write file", e.toString());
        }
    }


    private static String readFromFile(File file) {
        String fileContent = "";

        try {
            InputStream inputStream = SMSForwarderCommon.defaultContext.openFileInput(file.getPath());

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                fileContent = stringBuilder.toString();
            }
        } catch (IOException e) {
            Log.e("read file", e.toString());
        }

        return fileContent;
    }
}
