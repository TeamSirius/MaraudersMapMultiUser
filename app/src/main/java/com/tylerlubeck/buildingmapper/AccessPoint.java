package com.tylerlubeck.buildingmapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by brettfischlerandkennethwapman on 10/11/14.
 */
public class AccessPoint implements Comparable<AccessPoint>{
    private String bssid;
    private double rss;
    private Date date;
    private double std_dev;
    private int loc_id;

    AccessPoint(String bssid, double rss, Date date)
    {
        this.bssid = bssid;
        this.rss = rss;
        this.date = date;
        this.std_dev = -1;
        this.loc_id = -1;
    }

    void save(int loc_id, double std_dev)
    {
        this.loc_id = loc_id;
        this.std_dev = std_dev;
        // Save to table
    }

    String getBSSID()
    {
        return bssid;
    }

    double getRSS()
    {
        return rss;
    }

    @Override
    public int compareTo(AccessPoint accessPoint) {
        return Double.compare(this.rss, accessPoint.getRSS());
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject representation = new JSONObject();
        representation.put("MAC", this.bssid);
        representation.put("strength", this.rss);
        return representation;
    }

    @Override
    public String toString() {
        return String.format("%s: %f", this.bssid, this.rss);
    }
}
