package com.tylerlubeck.maraudersmapmultiuser;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

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
                Log.d("MARAUDERSMAP", "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d("MARAUDERSMAP", "Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                dispatchNotifications(extras.getString("msg"));
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void dispatchNotifications (String msg) {
        try {
            JSONObject object = new JSONObject(msg);
            String type = object.getString("type");
            if (type.equals("request_location")) {
                requestLocationNotification(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestLocationNotification(JSONObject msg) {
        try {
            String requestor_name = msg.getString("requestor");
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent replyIntent = new Intent(this, replyButtonListener.class);
            PendingIntent pendingReplyIntent = PendingIntent.getBroadcast(this,
                    0,
                    replyIntent,
                    0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("WHERE YOU AT")
                    .setContentText(String.format("%s wants to know where you are", requestor_name))
                    .addAction(R.drawable.ic_launcher, "Reply", pendingReplyIntent);
            notificationManager.notify(1, builder.build());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class replyButtonListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MARAUDERSMAP", "NEED TO REPLY");
            Toast.makeText(context, "REPLYING", Toast.LENGTH_LONG).show();
        }
    }
}
