package com.tylerlubeck.maraudersmapmultiuser;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

/**
 * Created by Tyler on 2/21/2015.
 */
public class FacebookLoginFragment extends Fragment {
    private LoginButton loginButton;
    private TextView username;
    private UiLifecycleHelper uiHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.facebook_login,
                                                                    container,
                                                                    false);

        loginButton = (LoginButton) linearLayout.findViewById(R.id.facebook_login_button);
        username = (TextView) linearLayout.findViewById(R.id.username);
        uiHelper = new UiLifecycleHelper(this.getActivity(), this.statusCallback);

        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser graphUser) {
                if (graphUser != null) {
                    username.setText("You are currently logged in as " + graphUser.getName());
                } else {
                    username.setText("You are not logged in");
                }
            }
        });

        return linearLayout;
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {

        @Override
        public void call(Session session, SessionState sessionState, Exception e) {
            if (sessionState.isOpened()) {
                Log.d("MARAUDERSMAP", "Facebook session opened");
            } else if (sessionState.isClosed()) {
                Log.d("MARAUDERSMAP", "Facebook session closed");
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
