package com.example.smsreciever;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private EditText targetURLEditText;
    private EditText portEditText;

    private Button saveAndCloseButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        
        //init UI elements
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setText(SMSForwarderCommon.settings.get(SMSForwarderCommon.Setting.senderPhoneNumber));

        targetURLEditText = findViewById(R.id.serverEditText);
        targetURLEditText.setText(SMSForwarderCommon.settings.get(SMSForwarderCommon.Setting.targetURL));

        portEditText = findViewById(R.id.portEditText);
        portEditText.setText(SMSForwarderCommon.settings.get(SMSForwarderCommon.Setting.port));

        saveAndCloseButton = findViewById(R.id.saveAndCloseButton);

        saveAndCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileHandler.writeSettingsToFile(getAllUserInputSettings());
                finish();
            }
        });
    }

    private HashMap<SMSForwarderCommon.Setting, String> getAllUserInputSettings() {
        HashMap<SMSForwarderCommon.Setting, String> allSettings = new HashMap<>();
        allSettings.put(SMSForwarderCommon.Setting.senderPhoneNumber, phoneNumberEditText.getText().toString().replaceAll("\\s", ""));
        allSettings.put(SMSForwarderCommon.Setting.targetURL, targetURLEditText.getText().toString());
        allSettings.put(SMSForwarderCommon.Setting.port, portEditText.getText().toString());

        return allSettings;
    }
}
