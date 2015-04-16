package com.tylerlubeck.maraudersmapmultiuser.Tasks;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Tyler on 3/29/2015.
 */
public class PostResponseToRequestTask extends GenericRespondToRequestTask {

    public PostResponseToRequestTask(String url, JSONObject response, String username, String password) {
        super(url, response, username, password);
    }

    @Override
    void processResponse(HttpEntity entity) {
        Log.d("MARAUDERSMAP", "Response sent successfully");
    }

    @Override
    void handleResponseException(int statusCode, HttpEntity entity) {
        try {
            String content = EntityUtils.toString(entity);
            Log.d("MARAUDERSMAP", "ERROR: " + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
