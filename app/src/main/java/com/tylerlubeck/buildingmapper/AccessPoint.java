package com.tylerlubeck.buildingmapper;

import java.util.Date;

/**
 * Created by brettfischlerandkennethwapman on 10/11/14.
 */
public class AccessPoint {
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
}
