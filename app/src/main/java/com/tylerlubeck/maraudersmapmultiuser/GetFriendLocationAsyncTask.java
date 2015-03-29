package com.tylerlubeck.maraudersmapmultiuser;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Tyler on 2/24/2015.
 */
public class GetFriendLocationAsyncTask extends GenericPOSTAPIKeyTask {


    GetFriendLocationAsyncTask(String _url, JSONObject data, String username, String api_key) {
        super(_url, data, username, api_key);
    }

    @Override
    void processResponse(String response) {
        Log.d("MARAUDERSMAP", "REQUEST WENT THROUGH");
    }

    @Override
    void handleResponseException(HttpResponseException responseException) {
        Log.e("MARAUDERSMAP", responseException.toString());
    }
}
