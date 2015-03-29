package com.tylerlubeck.maraudersmapmultiuser;

import org.apache.http.client.HttpResponseException;
import org.json.JSONObject;

/**
 * Created by Tyler on 3/29/2015.
 */
public class PostResponseToRequestTask extends GenericPOSTAPIKeyTask {

    PostResponseToRequestTask(String url, JSONObject response, String username, String password) {
        super(url, response, username, password);
    }

    @Override
    void processResponse(String response) {

    }

    @Override
    void handleResponseException(HttpResponseException responseException) {

    }
}
