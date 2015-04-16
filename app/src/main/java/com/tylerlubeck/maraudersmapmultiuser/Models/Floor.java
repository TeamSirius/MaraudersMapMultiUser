package com.tylerlubeck.maraudersmapmultiuser.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tyler Lubeck on 10/28/2014.
 */

/**
 * A class to represent a Floor
 */
public class Floor {
    private String building_name;
    private int floor_number;
    private String resource_uri;

    public Floor(String _building_name, int _floor_number) {
        this.building_name = _building_name;
        this.floor_number = _floor_number;
        this.resource_uri = "";
    }

    /**
     * Builds a floor from a JSON Blob
     * @param jsonRepresentation    A JSON Object that represents a Floor.
     *                                 Needs to have "building_name", "floor_number", and
     *                                  "resource_uri"
     */
    public Floor(JSONObject jsonRepresentation) {
        try {
            this.building_name = jsonRepresentation.getString("building_name");
            this.floor_number = jsonRepresentation.getInt("floor_number");
            this.resource_uri = jsonRepresentation.getString("resource_uri");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return String.format("%s Floor %d", this.building_name, this.floor_number);
    }

    public String getResourceUri() {
        return this.resource_uri;
    }

    public int getFloor() {
        return this.floor_number;
    }

    public String getBuilding() {
        return this.building_name;
    }
}
