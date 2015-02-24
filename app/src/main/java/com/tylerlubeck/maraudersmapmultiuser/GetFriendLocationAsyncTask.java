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
public class GetFriendLocationAsyncTask extends GenericGETTask {


    GetFriendLocationAsyncTask(String _url, List<BasicNameValuePair> _params) {
        super(_url, _params);
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
