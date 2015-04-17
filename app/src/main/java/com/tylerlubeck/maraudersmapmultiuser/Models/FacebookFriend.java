package com.tylerlubeck.maraudersmapmultiuser.Models;

import android.util.Log;

import com.tylerlubeck.maraudersmapmultiuser.Activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tyler on 2/23/2015.
 */
public class FacebookFriend {
    private String name;
    private String friend_fb_id;

    public FacebookFriend(JSONObject friend) {
        try {
            this.name = friend.getString("name");
            this.friend_fb_id = friend.getString("id");
        } catch (JSONException e) {
            this.name = "";
            this.friend_fb_id = "";
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.friend_fb_id;
    }

    public String toString() {
        return String.format("%s: %s", this.name, this.friend_fb_id);
    }
}
