package com.tylerlubeck.maraudersmapmultiuser;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Tyler on 2/23/2015.
 */
public class CreateUserAsyncTask extends GenericGETTask {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String SENDER_ID;

    private Context context;
    private Context appContext;
    private String regid;
    private GoogleCloudMessaging gcm;
    private AtomicInteger msgId = new AtomicInteger();
    private SharedPreferences gcmPrefs;

    CreateUserAsyncTask(Context _context, String _url, List<BasicNameValuePair> _params) {
        super(_url, _params);
        this.context = _context;
        this.appContext = _context.getApplicationContext();
        this.SENDER_ID = context.getString(R.string.GCM_SENDER_ID);
    }

    @Override
    void processResponse(String response) {
        try {
            //SharedPreferences preferences = getSharedPreferences();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.appContext);

            JSONObject response_data = new JSONObject(response);
            String facebook_username = response_data.getString("username");
            String api_key = response_data.getString("api_key");
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("facebook_username", facebook_username);
            editor.putString("api_key", api_key);
            editor.commit();
            Log.d("MARAUDERSMAP", String.format("%s: %s", response_data.getString("username"), response_data.getString("api_key")));

            registerForGCM(facebook_username, api_key);



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    void handleResponseException(HttpResponseException responseException) {
        Log.e("MARAUDERSMAP", responseException.toString());
    }

    private void registerForGCM(String username, String api_key) {
        appContext = this.context.getApplicationContext();
        gcm = GoogleCloudMessaging.getInstance(this.context);
        String regid = getRegistrationId(appContext);

        if (regid.isEmpty()) {
            registerInBackground(username, api_key);
        }
    }

    private String getRegistrationId(Context appContext) {
        final SharedPreferences prefs = this.getSharedPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.isEmpty()) {
            Log.i("MARAUDERSMAP", "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = this.getAppVersion(appContext);

        if (registeredVersion != currentVersion) {
            Log.i("MARAUDERSMAP", "App version changed");
            return "";
        }

        return registrationId;
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    private static int getAppVersion(Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground(final String username, final String api_key) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(appContext);
                    }
                    regid = gcm.register(SENDER_ID);
                    String msg = String.format("Device registered, ID is %s", regid);
//                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    Log.e("MARAUDERSMAP", msg);

                    sendRegistrationIdToBackend(regid, username, api_key);
                    // storeRegistrationId(context, regid);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend(String regid, String username, String api_key) {
        try {
            JSONObject object = new JSONObject();
            object.put("reg_id", regid);
            object.put("dev_id", username);
            new RegisterDeviceAsyncTask(this.context.getString(R.string.device_registraion_endpoint),
                                        object,
                                        username,
                                        api_key).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
