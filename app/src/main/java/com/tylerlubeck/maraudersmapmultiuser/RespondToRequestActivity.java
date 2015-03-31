package com.tylerlubeck.maraudersmapmultiuser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RespondToRequestActivity extends Activity implements View.OnClickListener {

    TextView requestorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_to_request);

        Button allowBtn = (Button) findViewById(R.id.allow_response_btn);
        Button denyBtn = (Button) findViewById(R.id.deny_response_btn);
    }

    @Override
    public void onClick(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle extras = getIntent().getExtras();

        int requestor_id = extras.getInt("requestor_id");
        boolean allow = false;

        final JSONObject responseObject = new JSONObject();
        final String facebookUsername = sharedPreferences.getString("facebook_username", "");
        final String apiKey = sharedPreferences.getString("api_key", "");
        final String url = this.getString(R.string.respond_to_request_endpoint);

        switch (view.getId()) {
            case R.id.allow_response_btn:
                allow = true;
                break;
            case R.id.deny_response_btn:
                allow = false;
                break;
        }

        try {
            responseObject.put("allow", allow);
            responseObject.put("requestor_id", requestor_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (allow) {
            this.allowRequest(url, responseObject, facebookUsername, apiKey);
        } else {
            this.denyRequest(url, responseObject, facebookUsername, apiKey);
        }
    }

    void allowRequest(final String url, final JSONObject responseObject,
                      final String facebookUsername, final String apiKey) {
        Toast.makeText(this, "VIOLATING YOUR PRIVACY", Toast.LENGTH_LONG).show();
        new AccessPointManager(this) {
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
    }

    void denyRequest(final String url, final JSONObject responseObject,
                      final String facebookUsername, final String apiKey) {
        (new PostResponseToRequestTask(url, responseObject, facebookUsername, apiKey)).execute();
    }
}
