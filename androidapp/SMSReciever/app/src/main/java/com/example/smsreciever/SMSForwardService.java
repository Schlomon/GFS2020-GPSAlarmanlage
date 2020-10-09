package com.example.smsreciever;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;


public class SMSForwardService extends Service implements MessageListener {

    private static final int NOTIF_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();

        // Register sms listener
        MessageReceiver.bindListener(this);

        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                getString(R.string.NotifChannelId))
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running in background")
                .setContentIntent(pendingIntent)
                .build());
    }


    @Override
    public void messageReceived(String sender, String body) {
        if (sender.equals(SMSForwarderCommon.settings.get(SMSForwarderCommon.Setting.senderPhoneNumber))) {
            //Toast.makeText(this, "New Message Received: " + body, Toast.LENGTH_LONG).show();
            FileHandler.writeSMSToFile(sender, body);

            body = body.replaceAll("<", "{");
            body = body.replaceAll(">", "}");

            try {
                String dataToSend = new JSONObject(body).put("sender", sender).toString();
                new Client(SMSForwarderCommon.settings.get(SMSForwarderCommon.Setting.targetURL), Integer.parseInt(SMSForwarderCommon.settings.get(SMSForwarderCommon.Setting.port))).sendData(dataToSend);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
