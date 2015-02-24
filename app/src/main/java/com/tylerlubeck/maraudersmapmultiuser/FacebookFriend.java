package com.tylerlubeck.maraudersmapmultiuser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tyler on 2/23/2015.
 */
public class FacebookFriend {
    private String name;
    private String id;

    FacebookFriend(JSONObject friend) {
        try {
            this.name = friend.getString("name");
            this.id = friend.getString("id");
        } catch (JSONException e) {
            this.name = "";
            this.id = "";
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }
}
