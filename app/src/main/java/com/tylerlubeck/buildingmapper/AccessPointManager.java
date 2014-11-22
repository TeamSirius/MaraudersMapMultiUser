package com.tylerlubeck.buildingmapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brettfischler on 10/11/14.
 */
public class AccessPointManager {
    ArrayList<AccessPoint> points;
    WifiManager wifiManager;
    int num_times_called;
    int num_polls;
    Date last_scan;
    HashMap<String, ArrayList<Integer>> MAC_Aggregator;
    boolean upload;
    int location_id;
    Context context;
    FloorMapImage FImage;


    AccessPointManager(Context context, int _num_polls, boolean upload, int id, FloorMapImage FImage) {
        this.FImage = FImage;
        this.context = context;
        this.location_id = id;
        this.upload = upload;
        this.points = new ArrayList<AccessPoint>();
        this.wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        this.num_times_called = 0;
        this.num_polls = _num_polls;
        this.last_scan = new Date();
        this.MAC_Aggregator = new HashMap<String, ArrayList<Integer>>();
        context.registerReceiver(new WifiScanReceived(), new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        this.wifiManager.startScan();
    }

    private void findAccessPoints() {
        final List<ScanResult> results = this.wifiManager.getScanResults();
        for (final ScanResult r : results) {
            if (! this.MAC_Aggregator.containsKey(r.BSSID)) {
                this.MAC_Aggregator.put(r.BSSID, new ArrayList<Integer>());
            }
            this.MAC_Aggregator.get(r.BSSID).add(r.level);
        }
    }

    private double standard_deviation(ArrayList<Integer> strengths, double mean) {
        int numberOfStrengths = strengths.size();
        if (numberOfStrengths <= 1) {
            return 0;
        }

        double variance = 0;
        for(double strength: strengths) {
            variance += Math.pow(strength - mean, 2);
        }

        Log.d("BUILDINGMAPPER", "Variance is " + Double.toString(variance));

        variance /= (numberOfStrengths - 1);
        return Math.sqrt(variance);
    }

    private double mean_value(ArrayList<Integer> strengths) {
        int numberOfStrengths = strengths.size();
        if(numberOfStrengths <= 0) {
            return 0;
        }

        double sum = 0;
        for(double strength: strengths) {
            sum += strength;
        }
        Log.d("BUILDINGMAPPER", "Mean is: " + Double.toString(sum / (double)numberOfStrengths));
        return sum / (double)numberOfStrengths;
    }

    private JSONArray averageAccessPointsMap() {
        double mean;
        double stdDeviation;
        JSONArray all_objects = new JSONArray();
        for(Map.Entry<String, ArrayList<Integer>> entry: this.MAC_Aggregator.entrySet()) {
            JSONObject representation = new JSONObject();
            Log.d("BUILDINGMAPPER", entry.getKey() + ": " + entry.getValue().toString());
            mean = mean_value(entry.getValue());
            stdDeviation = standard_deviation(entry.getValue(), mean);
            try {
                representation.put("MAC", entry.getKey());
                representation.put("strength", mean);
                representation.put("std", stdDeviation);
                all_objects.put(representation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return all_objects;
    }

    public void saveAll(int loc_id) {
        for (AccessPoint p : this.points) {
            p.save(loc_id, 0);
        }
    }

    public void printAll() {
        for (AccessPoint p : this.points) {
            Log.d("BUILDINGMAPPER", p.getBSSID());
            Log.d("BUILDINGMAPPER", Double.toString(p.getRSS()));
        }
    }


    void postToServer(JSONArray data, int id) {
        JSONObject send_data = new JSONObject();
        try {
            send_data.put("APS", data);
            send_data.put("lid", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = this.context.getString(R.string.post_access_points);
        PostAccessPointsTask post_access_points = new PostAccessPointsTask(url,
                                                                           send_data,
                                                                           this.context,
                                                                           this.FImage);
        post_access_points.execute();
    }


    private class WifiScanReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(num_times_called < num_polls ) {
                findAccessPoints();
                /* Print Info */
                Date now = new Date();
                Log.d("BUILDINGMAPPER", Long.toString(now.getTime() - last_scan.getTime()));
                last_scan = now;
                Log.d("BUILDINGMAPPER", "Got wifi scan number" + num_times_called);
                wifiManager.startScan();
            }
            if (num_times_called == num_polls && upload) {
                JSONArray uploadable = averageAccessPointsMap();
                postToServer(uploadable, location_id);
            }
            num_times_called++;
        }
    }
}
