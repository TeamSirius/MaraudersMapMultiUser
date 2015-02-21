package com.tylerlubeck.maraudersmapmultiuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brettfischler on 10/11/14.
 * Edited by Tyler Lubeck on 2/16/15
 */
public class AccessPointManager {
    private enum UploadType {
        UPLOAD,
        QUERY
    }
    final static int NUM_MAPPING_POLLS = 10;
    final static int NUM_QUERY_POLLS = 2;

    private int num_times_called;
    private int num_polls;
    private final UploadType uploadType;
    private String username;
    private String password;
    private String location_uri;
    private Context context;
    private HashMap<String, ArrayList<Integer>> MAC_Aggregator;
    private FloorMapImage floor_image;
    private WifiManager wifiManager;
    private BroadcastReceiver broadcastReceiver;

    /**
     * Create an AccessPointManager that allows for uploading the access points to the related location
     * @param _context          The context to operate with
     * @param _floor_image      The FloorImage to display where you are on
     * @param _location_uri     The location uri to associate the AccessPoints with
     * @param username          The username to authenticate with
     * @param password          The password to authenticate with
     */
    AccessPointManager(Context _context, FloorMapImage _floor_image, String _location_uri,
                       String username, String password) {
        this.instantiate(_context, this.NUM_MAPPING_POLLS, _floor_image, username, password);
        this.location_uri = _location_uri;
        this.uploadType = UploadType.UPLOAD;
        this.wifiManager.startScan();
    }

    /**
     * Create an AccessPointManager that allows for uploading the access points to the related location
     * @param _context          The context to operate with
     * @param _floor_image      The FloorImage to display where you are on
     * @param username          The username to authenticate with
     * @param password          The password to authenticate with
     */
    AccessPointManager(Context _context, FloorMapImage _floor_image,
                       String username, String password) {
        this.instantiate(_context, this.NUM_QUERY_POLLS, _floor_image, username, password);
        this.uploadType = UploadType.QUERY;
        this.wifiManager.startScan();
    }

    /**
     * Instantiates the AccessPointManager.
     *      We have to do it this way and not with a private constructor so that we can set the
     *      uploadType and not have a race condition with starting the wifi scan
     * @param _context          The context to operate with
     * @param _floor_image      The FloorImage to display where you are on
     * @param username          The username to authenticate with
     * @param password          The password to authenticate with
     */
    private void instantiate(Context _context, int _num_polls, FloorMapImage _floor_image,
                             String username, String password) {
        this.username = username;
        this.password = password;
        this.floor_image = _floor_image;
        this.context = _context;
        this.num_polls = _num_polls;
        this.wifiManager = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
        this.num_times_called = 0;
        this.MAC_Aggregator = new HashMap<String, ArrayList<Integer>>();
        this.broadcastReceiver = new WifiScanReceived();
        this.context.registerReceiver(this.broadcastReceiver,
                new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    /**
     * Parse through the WiFi scan results and aggregate them in to a HashTable
     * The HashTable keys are the BSSIDs of the access points, and
     *      the values are lists of strengths associated with the BSSID
     */
    private void findAccessPoints() {
        final List<ScanResult> results = this.wifiManager.getScanResults();
        for (final ScanResult result : results) {
            if (! this.MAC_Aggregator.containsKey(result.BSSID)) {
                this.MAC_Aggregator.put(result.BSSID, new ArrayList<Integer>());
            }
            this.MAC_Aggregator.get(result.BSSID).add(result.level);
        }
    }

    /**
     * Compute the standard deviation of the strengths, with a specified mean
     * @param strengths     An Integer list of strength to find the standard deviation of
     * @param mean          The mean to compute the standard deviation around
     * @return the standard deviation
     */
    private double standard_deviation(ArrayList<Integer> strengths, double mean) {
        int strength_size = strengths.size();
        double variance = 0;

        /* If we have one or fewer strengths, there is no deviation */
        if (strength_size <= 1) {
            return 0;
        }

        for(int strength: strengths) {
            variance += Math.pow(strength - mean, 2);
        }

        variance /= (strength_size - 1);
        return Math.sqrt(variance);
    }

    /**
     * Compute the mean strength for a list of strengths
     * @param strengths     The list of strengths
     * @return the mean value of the strengths
     */
    private double mean_value(ArrayList<Integer> strengths) {
        int strength_size = strengths.size();
        double sum = 0;

        /* If there are no strengths, there is no mean */
        if(strength_size == 0) {
            return 0;
        }

        for(double strength: strengths) {
            sum += strength;
        }

        return sum / (double)strength_size;
    }

    /**
     *
     * @return a JSONArray of AccessPoint
     */
    private JSONArray averageAccessPointsMap(String location_uri) {
        double mean_signal_strength;
        double RSS_std_deviation;
        JSONArray all_access_points = new JSONArray();

        for(Map.Entry<String, ArrayList<Integer>> entry: this.MAC_Aggregator.entrySet()) {
            try {
                mean_signal_strength = mean_value(entry.getValue());
                RSS_std_deviation = standard_deviation(entry.getValue(), mean_signal_strength);

                AccessPoint accessPoint = new AccessPoint(entry.getKey(),
                                                          mean_signal_strength,
                                                          RSS_std_deviation,
                                                          location_uri);

                all_access_points.put(accessPoint.toJSON());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return all_access_points;
    }


    /**
     * Uploads the AccessPoints to the server for mapping purposes
     * @param accessPointData   The JSONArray of Access Point Information
     */
    void uploadNewPoints(JSONArray accessPointData) {

        try {
            String url = this.context.getString(R.string.accesspoint_endpoint);
            JSONObject data = new JSONObject();
            data.put("objects", accessPointData);
            PostAccessPointsTask post_access_points = new PostAccessPointsTask(url,
                                                                               data,
                                                                               this.context,
                                                                               this.floor_image,
                                                                               this.username,
                                                                               this.password);
            post_access_points.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uploads the AccessPoints to the server for the purpose of finding the user's location
     * @param accessPointData   The JSONArray of Access Point Information
     */
    void findMyPosition(JSONArray accessPointData) {
        String url = this.context.getString(R.string.locate_me_endpoint);
        JSONObject data = new JSONObject();
        try {
            data.put("objects", accessPointData);
            PostMyLocationAsyncTask postMyLocationAsyncTask = new PostMyLocationAsyncTask(url,
                                                                                          data,
                                                                                          this.floor_image,
                                                                                          this.username,
                                                                                          this.password,
                                                                                          this.context);
            postMyLocationAsyncTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * A Broadcast Receiver class to listen for WifiScanReceived events.
     * When a scan is received, it determines if we have collected the number of required scans.
     *      If so, it averages the access point information and uploads it to the server
     *      If not, it adds the data seen to the HashMap of BSSID: Signal strength pairs and then
     *          starts another scan
     */
    private class WifiScanReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /* Get a reference to the containing class, so that we can use the variables */
            AccessPointManager outerAPM = AccessPointManager.this;

            if(outerAPM.num_times_called < outerAPM.num_polls ) {
                outerAPM.findAccessPoints();
                outerAPM.wifiManager.startScan();
            }

            /* If we've performed the correct number of scans... */
            if (outerAPM.num_times_called == outerAPM.num_polls) {
                /* Immediately unregister the receiver so that we don't listen for any more */
                outerAPM.context.unregisterReceiver(outerAPM.broadcastReceiver);
                JSONArray uploadable = averageAccessPointsMap(outerAPM.location_uri);

                Toast.makeText(outerAPM.context,
                        outerAPM.num_times_called + " scans done, uploading",
                        Toast.LENGTH_SHORT)
                        .show();

                /* Either do one or the other */
                if (outerAPM.uploadType == UploadType.UPLOAD){
                    /* Upload the points and associate them with this location */
                    uploadNewPoints(uploadable);
                } else if (outerAPM.uploadType == UploadType.QUERY) {
                    /* Use the seen points to compute our location */
                    findMyPosition(uploadable);
                }
            }
            outerAPM.num_times_called++;

            /* For debugging purposes, inform the user after every 5 scans */
            if (outerAPM.num_times_called % 5 == 0) {
                Toast.makeText(outerAPM.context,
                               "Did scan " + outerAPM.num_times_called,
                               Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
