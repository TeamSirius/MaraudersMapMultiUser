package com.tylerlubeck.maraudersmapmultiuser;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Tyler on 2/23/2015.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification("Received: " + extras.toString());
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                                                                0,
                                                                new Intent(this, MainActivity.class),
                                                                0);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setSmallIcon(R.drawable.ic_launcher)
               .setContentTitle("SUCCESSFUL MESSAGE")
               .setContentText(msg);

        Notification notification = builder.getNotification();
        notificationManager.notify(NOTIFICATION_ID, notification);


    }
}
