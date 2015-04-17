package com.tylerlubeck.maraudersmapmultiuser.Fragment;

import android.app.ListFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.tylerlubeck.maraudersmapmultiuser.API.PositioningService;
import com.tylerlubeck.maraudersmapmultiuser.API.RequestInterceptors;
import com.tylerlubeck.maraudersmapmultiuser.Activities.MainActivity;
import com.tylerlubeck.maraudersmapmultiuser.Models.FacebookFriend;
import com.tylerlubeck.maraudersmapmultiuser.R;
import com.tylerlubeck.maraudersmapmultiuser.Adapters.SelectFriendsArrayAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

/**
 * Created by Tyler on 2/23/2015.
 */
public class SelectFriendFragment extends ListFragment {

    ArrayList<FacebookFriend> friendsList;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View headerView = getActivity().getLayoutInflater().inflate(R.layout.list_header, null);
        getListView().addHeaderView(headerView);

        this.friendsList = new ArrayList<FacebookFriend>();
        this.fillList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setListAdapter(null);
    }

    private void fillList() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) Log.d("MARAUDERSMAP", "ACCESS TOKEN IS NULL, MAKING REQUEST");
        GraphRequest request = GraphRequest.newMyFriendsRequest(accessToken,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                        int numFriends = jsonArray.length();
                        for(int i = 0; i < numFriends; i++){
                            try {
                                FacebookFriend friend = new FacebookFriend(jsonArray.getJSONObject(i));
                                SelectFriendFragment.this.friendsList.add(friend);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        SelectFriendsArrayAdapter friendsArrayAdapter = new SelectFriendsArrayAdapter(getActivity(), friendsList);
                        SelectFriendFragment.this.setListAdapter(friendsArrayAdapter);
                    }
                }
        );

        request.executeAsync();
    }

    private PositioningService getPositioningService() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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



    @Override
    public void onListItemClick(final ListView listView, View view, int position, long id) {
        if (position == 0) {
            /* If this is the header view, stop processing */
            return;
        }
        final FacebookFriend fbFriend = (FacebookFriend) listView.getItemAtPosition(position);
        PositioningService positioningService = getPositioningService();
        positioningService.findFriend(fbFriend, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                Log.d(MainActivity.LOG_TAG, "FRIEND REQUEST WENT THROUGH");
                Toast.makeText(getActivity(),
                        String.format("Asking %s for their location...", fbFriend.getName()),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                NotificationManager notificationManager = (NotificationManager) getActivity()
                                                                .getSystemService(Context.NOTIFICATION_SERVICE);
                Log.e(MainActivity.LOG_TAG, "Unable to request friend's position");
                Log.e(MainActivity.LOG_TAG, error.getUrl());
                Log.e(MainActivity.LOG_TAG, error.getResponse().getReason());
                Notification.Builder builder = new Notification.Builder(getActivity())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Couldn't find your friend")
                        .setContentText(String.format("Couldn't find %s", fbFriend.getName()));

                notificationManager.notify(1, builder.getNotification());
            }
        });
    }
}
