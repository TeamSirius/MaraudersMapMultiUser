package com.tylerlubeck.maraudersmapmultiuser;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
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
            Intent allowReplyIntent = new Intent(this, replyButtonListener.class);
            allowReplyIntent.putExtra("requestor_id", msg.getString("requestor_id"));
            allowReplyIntent.putExtra("allow", true);
            PendingIntent pendingAllowReplyIntent = PendingIntent.getBroadcast(this,
                    0,
                    allowReplyIntent,
                    0);
            Intent disallowReplyIntent = new Intent(this, replyButtonListener.class);
            int requestor_id = msg.getInt("requestor_id");
            disallowReplyIntent.putExtra("requestor_id", requestor_id);
            disallowReplyIntent.putExtra("allow", false);
            PendingIntent pendingDisallowReplyIntent = PendingIntent.getBroadcast(this,
                    0,
                    allowReplyIntent,
                    0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("WHERE YOU AT")
                    .setContentText(String.format("%s wants to know where you are", requestor_name))
                    .addAction(R.drawable.ic_launcher, "Allow", pendingAllowReplyIntent)
                    .addAction(R.drawable.ic_launcher, "Deny", pendingDisallowReplyIntent)
                    .setAutoCancel(true);
            notificationManager.notify(requestor_id, builder.build());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class replyButtonListener extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            int requestor_id = extras.getInt("requestor_id");
            boolean allow = extras.getBoolean("allow");

            final JSONObject responseObject = new JSONObject();
            final String facebookUsername = sharedPreferences.getString("facebook_username", "");
            final String apiKey = sharedPreferences.getString("api_key", "");
            final String url = context.getString(R.string.respond_to_request_endpoint);

            try {
                responseObject.put("allow", allow);
                responseObject.put("requestor_id", requestor_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (allow) {
                Toast.makeText(context, "VIOLATING YOUR PRIVACY", Toast.LENGTH_LONG).show();
                /*
                new AccessPointManager(context) {
                    @Override
                    protected void allDataReceived(JSONArray accessPointData) {
                        try {
                            responseObject.put("access_points", accessPointData);
                            (new PostResponseToRequestTask(url, responseObject, facebookUsername, apiKey)).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                */
            } else {
                (new PostResponseToRequestTask(url, responseObject, facebookUsername, apiKey)).execute();
            }
        }
    }
}
