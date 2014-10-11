package com.tylerlubeck.buildingmapper;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by brettfischler on 10/11/14.
 */
public class AccessPointManager {
    ArrayList<AccessPoint> points;
    WifiManager wifiManager;

    AccessPointManager(Context context)
    {
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        points = findAccessPoints();
    }

    private ArrayList<AccessPoint> findAccessPoints()
    {
        ArrayList<AccessPoint> aps = new ArrayList<AccessPoint>();
        final List<ScanResult> results = wifiManager.getScanResults();
        for (final ScanResult r : results) {
            aps.add(new AccessPoint(r.BSSID, r.level, new Date()));
        }
        return aps;
    }

    public void saveAll(int loc_id)
    {
        for (AccessPoint p : points) {
            p.save(loc_id, 0);
        }
    }

    public void printAll()
    {
        for (AccessPoint p : points) {
            Log.d("BUILDINGMAPPER", p.getBSSID());
            Log.d("BUILDINGMAPPER", Double.toString(p.getRSS()));
        }
    }
}
