package com.tylerlubeck.buildingmapper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tyler on 10/28/2014.
 */
public class Floor {
    private String building;
    private int floor;

    Floor(String _building, int _floor) {
        this.building = _building;
        this.floor = _floor;
    }

    Floor(JSONObject obj) {
        try {
            this.building = obj.getString("building");
            this.floor = obj.getInt("floor");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return String.format("%s Floor %d", this.building, this.floor);
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }
}
