package com.tylerlubeck.buildingmapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by brettfischler on 10/11/14.
 */
public class AccessPointManager {
    ArrayList<AccessPoint> points;
    WifiManager wifiManager;
    int num_times_called;
    int num_polls;
    Date last_scan;

    AccessPointManager(Context context, int _num_polls)
    {
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        points = findAccessPoints();
        num_times_called = 0;
        this.num_polls = _num_polls;
        last_scan = new Date();
        context.registerReceiver(new WifiScanReceived(), new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    private ArrayList<AccessPoint> findAccessPoints()
    {
        ArrayList<AccessPoint> aps = new ArrayList<AccessPoint>();
        final List<ScanResult> results = wifiManager.getScanResults();
        for (final ScanResult r : results) {
            aps.add(new AccessPoint(r.BSSID, r.level, new Date()));
        }
        Collections.sort(aps, Collections.reverseOrder());
        return aps;
    }

    public ArrayList<AccessPoint> getAccessPoints() {
        return this.findAccessPoints();
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

    private class WifiScanReceived extends BroadcastReceiver {
        //TODO: Do we need to register receiver in Manifest?

        @Override
        public void onReceive(Context context, Intent intent) {
            if(num_times_called <= num_polls ) {
                Date now = new Date();
                Log.d("BUILDINGMAPPER", Long.toString(now.getTime() - last_scan.getTime()));
                last_scan = now;
                Log.d("BUILDINGMAPPER", "Got wifi scan number" + num_times_called);
                Log.d("BUILDINGMAPPER", String.valueOf(findAccessPoints()));
                wifiManager.startScan();
            }
            num_times_called++;
        }
    }
}
