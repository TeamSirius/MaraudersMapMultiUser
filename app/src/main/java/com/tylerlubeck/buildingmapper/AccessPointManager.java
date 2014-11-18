package com.tylerlubeck.buildingmapper;

import android.content.Context;
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

    AccessPointManager(Context context)
    {
        this.wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        this.points = findAccessPoints();
    }

    private ArrayList<AccessPoint> findAccessPoints()
    {
        ArrayList<AccessPoint> aps = new ArrayList<AccessPoint>();
        final List<ScanResult> results = this.wifiManager.getScanResults();
        for (final ScanResult r : results) {
            aps.add(new AccessPoint(r.BSSID, r.level, new Date()));
        }
        Collections.sort(aps, Collections.reverseOrder());
        return aps;
    }


    private double standard_deviation(ArrayList<Double> strengths, double mean) {
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

    private double mean_value(ArrayList<Double> strengths) {
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


    private HashMap<String, ArrayList<Double>> aggregateAccessPoints(int numberOfPolls) {
        HashMap<String, ArrayList<Double>>  MAC_Aggregator = new HashMap<String, ArrayList<Double>>();
        String points_MAC;


        for(int i = 0; i < numberOfPolls; i++) {
            this.wifiManager.startScan();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d("BUILDINGMAPPER", "RUN " + i);
            ArrayList<AccessPoint> points_this_poll = this.findAccessPoints();
            for(AccessPoint point : points_this_poll) {
                points_MAC = point.getBSSID();
                if (! MAC_Aggregator.containsKey(points_MAC)) {
                    MAC_Aggregator.put(points_MAC, new ArrayList<Double>());
                }
                MAC_Aggregator.get(points_MAC).add(point.getRSS());
            }

        }
        return MAC_Aggregator;
    }

    private JSONArray averageAccessPointsMap(HashMap<String, ArrayList<Double>> accessPointsMap) {
        double mean;
        double stdDeviation;
        JSONArray all_objects = new JSONArray();
        for(Map.Entry<String, ArrayList<Double>> entry: accessPointsMap.entrySet()) {
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

    public JSONArray pollAccessPoints(int numberOfPolls) {
        HashMap<String, ArrayList<Double>> accessPointsMap = aggregateAccessPoints(numberOfPolls);
        return averageAccessPointsMap(accessPointsMap);
    }

    public ArrayList<AccessPoint> getAccessPoints() {
        return this.findAccessPoints();
    }

    public void saveAll(int loc_id)
    {
        for (AccessPoint p : this.points) {
            p.save(loc_id, 0);
        }
    }

    public void printAll()
    {
        for (AccessPoint p : this.points) {
            Log.d("BUILDINGMAPPER", p.getBSSID());
            Log.d("BUILDINGMAPPER", Double.toString(p.getRSS()));
        }
    }
}
