package com.tylerlubeck.maraudersmapmultiuser.Activities;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tylerlubeck.maraudersmapmultiuser.GcmBroadcastReceiver;
import com.tylerlubeck.maraudersmapmultiuser.R;

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
        Log.d("MARAUDERSMAP", msg);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            JSONObject object = new JSONObject(msg);
            String type = object.getString("type");
            if (type.equals("request_location")) {
                requestLocationNotification(object);
            } else if (type.equals("request_granted")) {
                requestGranted(object);
            } else if (type.equals("request_denied")) {
                requestDenied(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestLocationNotification(JSONObject msg) {
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            String requestor_name = msg.getString("requestor");
            int requestor_id = msg.getInt("requestor_id");
            Intent replyIntent = new Intent(this, RespondToRequestActivity.class);
            replyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            replyIntent.putExtra("requestor_id", requestor_id);
            replyIntent.putExtra("requestor", requestor_name);

            PendingIntent pendingReplyIntent = PendingIntent.getActivity(this,
                    0,
                    replyIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Location Request Received!")
                    .setContentText(String.format("%s wants to know where you are", requestor_name))
                    .setContentIntent(pendingReplyIntent)
                    .setAutoCancel(true);
            notificationManager.notify(requestor_id, builder.build());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestGranted(JSONObject msg) {
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        try {

            String friend_name = msg.getString("friend");
            int x_coordinate = msg.getInt("x_coordinate");
            int y_coordinate = msg.getInt("y_coordinate");
            String image_url = msg.getString("image_url");
            int friend_id = msg.getInt("friend_id");

            Bundle extras = new Bundle();

            extras.putString("friend_name", friend_name);
            extras.putInt("x_coordinate", x_coordinate);
            extras.putInt("y_coordinate", y_coordinate);
            extras.putString("image_url", image_url);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtras(extras);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(String.format("Found %s!", friend_name))
                    .setContentText("And we promise they're up to no good.")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            notificationManager.notify(friend_id, builder.getNotification());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestDenied(JSONObject msg) {
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        String friend_name = null;
        try {
            friend_name = msg.getString("friend");
            int friend_id = msg.getInt("friend_id");
            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Location Request Denied")
                    .setContentText(String.format("%s is feeling private", friend_name))
                    .setAutoCancel(true);
            notificationManager.notify(friend_id, builder.getNotification());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
