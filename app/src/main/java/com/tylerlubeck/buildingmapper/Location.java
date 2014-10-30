package com.tylerlubeck.buildingmapper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tyler on 10/29/2014.
 */
public class Location {
    String verbose_name;
    int id;

    Location(String _name, int _id) {
        this.id = _id;
        this.verbose_name = _name;
    }

    Location(JSONObject obj) throws JSONException {
        this.id = obj.getInt("id");
        this.verbose_name = obj.getString("verbose_name");
    }

    public String toString() {
        return String.format("%d: %s", this.id, this.verbose_name);
    }

    public String getVerboseName() {
        return verbose_name;
    }

    public void setVerboseName(String verbose_name) {
        this.verbose_name = verbose_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
