package com.tylerlubeck.maraudersmapmultiuser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LoginActivity extends Activity {

    private SharedPreferences preferences;
    private CallbackManager callbackManager;
    private Button regularLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.app_name));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.facebook_login);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.callbackManager = CallbackManager.Factory.create();
        this.regularLoginButton = (Button) findViewById(R.id.login_button);


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("BUILDINGMAPPER", "LOGIN SUCCESS");
                String tokenString = loginResult.getAccessToken().getToken().toString();
                SharedPreferences.Editor editor = LoginActivity.this.preferences.edit();
                editor.putString("facebook_token", tokenString);
                editor.commit();

                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("access_token", tokenString));

                new CreateUserAsyncTask(LoginActivity.this,
                        getString(R.string.account_creation_endpoint),
                        params).execute();
            }

            @Override
            public void onCancel() {
                Log.d("BUILDINGMAPPER", "LOGIN CANCEL");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("BUILDINGMAPPER", "LOGIN ERROR" + e.toString());
            }
        });

        this.regularLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
