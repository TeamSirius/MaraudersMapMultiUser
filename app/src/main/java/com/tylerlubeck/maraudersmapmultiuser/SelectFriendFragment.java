package com.tylerlubeck.maraudersmapmultiuser;

import android.app.ListFragment;
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
import com.facebook.HttpMethod;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tyler on 2/23/2015.
 */
public class SelectFriendFragment extends ListFragment {

    ArrayList<FacebookFriend> friendsList;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        this.friendsList = new ArrayList<FacebookFriend>();
        super.onActivityCreated(savedInstanceState);
        this.fillList();
    }

    private void fillList() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) Log.d("MARAUDERSMAP", "ACCESS TOKEN IS NULL, MAKING REQUEST");
        GraphRequest request = GraphRequest.newMyFriendsRequest(accessToken,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                        int numFriends = jsonArray.length();
                        Log.d("MARAUDERSMAP", "YOU HAVE THIS MANY FRIENDS: " + Integer.toString(numFriends));
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

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        FacebookFriend fbFriend = friendsList.get(position);
        Toast.makeText(this.getActivity(), "Selected Friend: " + fbFriend.getName(), Toast.LENGTH_LONG).show();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = preferences.getString("facebook_username", "");
        String api_key = preferences.getString("api_key", "");

        if (username.isEmpty() || api_key.isEmpty()) {
            Log.e("MARAUDERSMAP", "Could not get a username or api key");
        }

        JSONObject data = new JSONObject();
        try {
            data.put("friend_fb_id", fbFriend.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetFriendLocationAsyncTask(getActivity().getString(R.string.get_friend_location_endpoint),
                                       data, username, api_key).execute();
        getFragmentManager().popBackStackImmediate();
    }
}
