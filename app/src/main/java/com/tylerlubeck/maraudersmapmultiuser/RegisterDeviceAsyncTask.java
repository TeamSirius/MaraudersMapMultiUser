package com.tylerlubeck.maraudersmapmultiuser;

import android.util.Log;

import org.apache.http.client.HttpResponseException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tyler on 2/23/2015.
 */
public class RegisterDeviceAsyncTask extends GenericPOSTAPIKeyTask {

    RegisterDeviceAsyncTask(String _url, JSONObject _data, String username, String password) {
        super(_url, _data, username, password);
    }

    @Override
    void processResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            Log.d("MARAUDERSMAP", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    void handleResponseException(HttpResponseException responseException) {
        Log.e("MARAUDERSMAP", responseException.toString());
    }
}
