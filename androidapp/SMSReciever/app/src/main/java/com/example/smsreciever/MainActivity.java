
package com.example.smsreciever;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private Button startStopButton;
    private Button enterSettingsButton;
    private Button requestSMSButton;

    // TODO remove
    //private Button debugButton;

    private TextView lastReceivedSMSTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Make context available to all classes
        SMSForwarderCommon.defaultContext = this;

        SMSForwarderCommon.refreshSettings();

        //Check if permissions are granted, if not request them
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.FOREGROUND_SERVICE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, 1);
        }



        //Start SMS forwarding
        if (!SMSForwarderCommon.serviceIsRunning) {
            createNotificationChannel();
            startService(new Intent(this, SMSForwardService.class));
            SMSForwarderCommon.serviceIsRunning = true;
        }


        //init UI elements
        startStopButton = findViewById(R.id.startStopButton);
        enterSettingsButton = findViewById(R.id.enterSettingsButton);
        lastReceivedSMSTextView = findViewById(R.id.receivedSMSContentTextView);
        requestSMSButton = findViewById(R.id.smsRequest);

        // TODO remove
//        debugButton = findViewById(R.id.debugButton);

            //set button text
        if (SMSForwarderCommon.serviceIsRunning) {
            startStopButton.setText(R.string.stopButton);
        } else {
            startStopButton.setText(R.string.startButton);
        }
            // set rec SMS text
        try {
            String lastSMS = FileHandler.readLastSMS();
            lastReceivedSMSTextView.setText(lastSMS);
        } catch (java.util.NoSuchElementException e) {
            lastReceivedSMSTextView.setText("No SMS reveived yet.");
        } catch (NullPointerException e) {
            lastReceivedSMSTextView.setText("No SMS reveived yet.");
        }


        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SMSForwarderCommon.serviceIsRunning) {
                    stopForwarding();
                } else {
                    startForwarding();
                }
            }
        });
        enterSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTour = new Intent(MainActivity.this, SettingsActivity.class);
                intentTour.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //This line is optional, better to use it because it won't create multiple instances of the launching Activity.
                startActivity(intentTour);
            }
        });
        requestSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = SMSForwarderCommon.settings.get(SMSForwarderCommon.Setting.senderPhoneNumber);
                SmsManager smsManager = SmsManager.getDefault();
                if (!phoneNum.equals("")) {
                    smsManager.sendTextMessage(phoneNum, null, "Neue Koordinaten bitte.", null, null);
                }
                toastRequestConfirmed();
            }
        });
    }

    private void toastRequestConfirmed() {
        Toast.makeText(this, "Requested new coordinate", Toast.LENGTH_SHORT).show();
    }

    private void startForwarding() {
        startStopButton.setText(R.string.stopButton);
        SMSForwarderCommon.serviceIsRunning = true;
        startService(new Intent(this, SMSForwardService.class));
        Toast.makeText(this, "Started!", Toast.LENGTH_SHORT).show();
    }

    private void stopForwarding() {
        startStopButton.setText(R.string.startButton);
        SMSForwarderCommon.serviceIsRunning = false;
        this.stopService(new Intent(this, SMSForwardService.class));
        Toast.makeText(this, "Stopped!", Toast.LENGTH_SHORT).show();
    }

    // wird benötigt um der App es zu erlauben auch im Hintergrund die SMS zu lesen und weiterzuleiten
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Keep alive";
            String description = "This channel will keep the forwarding alive.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.NotifChannelId), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}