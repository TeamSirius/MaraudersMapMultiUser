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
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.Facebook;
import com.facebook.model.GraphObject;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 2/23/2015.
 */
public class SelectFriendFragment extends ListFragment {

    ArrayList<FacebookFriend> friendsList;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String facebook_token = preferences.getString("facebook_token", "");
        if (facebook_token.isEmpty()) {
            Log.d("MARAUDERSMAP", "No Facebook Token");
            return;
        }
        //Session session = Session.openActiveSessionWithAccessToken(getActivity(), AccessToken.createFromExistingAccessToken(facebook_token, null, null, null, null), null);
        Session session = Session.getActiveSession();

        if (session == null || ! session.isOpened()) {
            Log.d("MARAUDERSMAP", "Session not open");
            return;
        }

        new Request(session,
                "/me/friends",
                null,
                HttpMethod.GET,
                new Request.Callback(){

                    @Override
                    public void onCompleted(Response response) {
                        GraphObject responseGraph = response.getGraphObject();
                        JSONObject object = responseGraph.getInnerJSONObject();
                        friendsList = new ArrayList<FacebookFriend>();
                        try {
                            JSONArray friends = object.getJSONArray("data");
                            int friends_length = friends.length();
                            for(int i = 0; i < friends_length; i++) {
                                Log.d("MARAUDERSMAP", String.format("%s: %s", friends.getJSONObject(i).getString("name"), friends.getJSONObject(i).getString("id")));
                                friendsList.add(new FacebookFriend(friends.getJSONObject(i)));
                            }

                            Log.d("MARAUDERSMAP", String.format("Found %d friends", friends_length));

                            SelectFriendsArrayAdapter friendsArrayAdapter = new SelectFriendsArrayAdapter(getActivity(), friendsList);
                            setListAdapter(friendsArrayAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).executeAsync();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        FacebookFriend fbFriend = friendsList.get(position);
        Toast.makeText(this.getActivity(), "Selected Friend: " + fbFriend.getName(), Toast.LENGTH_LONG).show();

        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("friend_id", fbFriend.getId()));

        new GetFriendLocationAsyncTask(getActivity().getString(R.string.get_friend_location_endpoint),
                                       params).execute();
    }
}
