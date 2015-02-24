package com.tylerlubeck.maraudersmapmultiuser;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tyler on 2/21/2015.
 */
public class FacebookLoginFragment extends Fragment {
    private LoginButton loginButton;
    private TextView username;
    private UiLifecycleHelper uiHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.facebook_login,
                                                                    container,
                                                                    false);

        loginButton = (LoginButton) linearLayout.findViewById(R.id.facebook_login_button);
        username = (TextView) linearLayout.findViewById(R.id.username);
        uiHelper = new UiLifecycleHelper(this.getActivity(), this.statusCallback);

        loginButton.setFragment(new NativeFragmentWrapper(this));
        loginButton.setReadPermissions(Arrays.asList("user_friends"));
        loginButton.setUserInfoChangedCallback(this.userInfoChangedCallback);

        uiHelper.onCreate(savedInstanceState);

        return linearLayout;
    }

    private LoginButton.UserInfoChangedCallback userInfoChangedCallback = new LoginButton.UserInfoChangedCallback() {

        @Override
        public void onUserInfoFetched(GraphUser graphUser) {
            if (graphUser != null) {
                username.setText("You are currently logged in as " + graphUser.getName());
                //getFragmentManager().popBackStack();
            } else {
                username.setText("You are not logged in");
            }
        }
    };

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {

        @Override
        public void call(Session session, SessionState sessionState, Exception e) {
            if (sessionState.isOpened()) {
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("access_token", session.getAccessToken()));
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("facebook_token", session.getAccessToken());
                editor.commit();

                new CreateUserAsyncTask(getActivity(),
                                        getString(R.string.account_creation_endpoint),
                                        params).execute();

            } else if (sessionState.isClosed()) {
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }


}
