package com.tylerlubeck.maraudersmapmultiuser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tylerlubeck.maraudersmapmultiuser.Models.AccessPoint;
import com.tylerlubeck.maraudersmapmultiuser.Tasks.PostResponseToRequestTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RespondToRequestActivity extends Activity implements View.OnClickListener {

    TextView requestorView;
    String requestor;
    int requestor_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_to_request);
        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            Log.e("MARAUDERSMAP", "No extras in respond to request activity");
        }
        this.requestor = extras.getString("requestor");
        this.requestor_id = extras.getInt("requestor_id");

        Button allowBtn = (Button) findViewById(R.id.allow_response_btn);
        Button denyBtn = (Button) findViewById(R.id.deny_response_btn);
        TextView textView = (TextView) findViewById(R.id.requestor_name_textview);

        textView.setText(String.format("%s wants to know where you are", this.requestor));

        allowBtn.setOnClickListener(this);
        denyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
            responseObject.put("allow_request", allow);
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
        /*
        new AccessPointManager(this) {
            @Override
            protected void allDataReceived(ArrayList<AccessPoint> accessPointData) {
                try {
                    Log.i("MARAUDERSMAP", "SENT LOCATION RESPONSE");
                    responseObject.put("access_points", accessPointData);
                    (new PostResponseToRequestTask(url, responseObject, facebookUsername, apiKey)).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }
        };
        */
    }

    void denyRequest(final String url, final JSONObject responseObject,
                      final String facebookUsername, final String apiKey) {
        (new PostResponseToRequestTask(url, responseObject, facebookUsername, apiKey)).execute();
        finish();
    }
}
