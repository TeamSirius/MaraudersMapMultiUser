package com.tylerlubeck.maraudersmapmultiuser.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.tylerlubeck.maraudersmapmultiuser.API.PositioningService;
import com.tylerlubeck.maraudersmapmultiuser.API.RequestInterceptors;
import com.tylerlubeck.maraudersmapmultiuser.AccessPointManager;
import com.tylerlubeck.maraudersmapmultiuser.Models.AccessPoint;
import com.tylerlubeck.maraudersmapmultiuser.Models.RespondToRequestBody;
import com.tylerlubeck.maraudersmapmultiuser.R;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;


public class RespondToRequestActivity extends Activity implements View.OnClickListener {

    TextView requestorView;
    String requestor;
    int requestor_id;
    ProgressBar progress;
    Button allowBtn;
    Button denyBtn;

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
        progress = (ProgressBar) findViewById(R.id.loading);

        allowBtn = (Button) findViewById(R.id.allow_response_btn);
        denyBtn = (Button) findViewById(R.id.deny_response_btn);
        TextView textView = (TextView) findViewById(R.id.requestor_name_textview);

        textView.setText(String.format("%s wants to know!", this.requestor));

        allowBtn.setOnClickListener(this);
        denyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        boolean allow = view.getId() == R.id.allow_response_btn;

        RespondToRequestBody respondToRequestBody = new RespondToRequestBody(allow, requestor_id);
        this.progress.setVisibility(View.VISIBLE);
        this.allowBtn.setEnabled(false);
        this.denyBtn.setEnabled(false);

        if (allow) {
            this.allowRequest(respondToRequestBody);
        } else {
            /* If we're not allowing, then we can just send the response */
            this.sendResponse(respondToRequestBody);
        }

    }

    private PositioningService getPositioningService() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String apiKey = preferences.getString("api_key", "");
        String facebookUsername = preferences.getString("facebook_username", "");
        RestAdapter restAdapter  = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.root_server))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog(MainActivity.LOG_TAG))
                .setRequestInterceptor(new RequestInterceptors.AuthorizedHeaderIntercepter(facebookUsername, apiKey))
                .build();

        return restAdapter.create(PositioningService.class);
    }


    void allowRequest(final RespondToRequestBody response) {
        new AccessPointManager(this) {
            @Override
            protected void allDataReceived(ArrayList<AccessPoint> accessPointData) {
                response.setAccessPoints(accessPointData);
                sendResponse(response);
            }
        };
    }

    void sendResponse(RespondToRequestBody response) {
        /*
        TODO: Show a loading message
         */
        PositioningService positioningService = getPositioningService();
        positioningService.respondToRequest(response, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                Toast.makeText(RespondToRequestActivity.this,
                        String.format("Notified %s!", requestor),
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Crashlytics.logException(error);
                Toast.makeText(RespondToRequestActivity.this,
                        String.format("Unable to notify %s", requestor),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
