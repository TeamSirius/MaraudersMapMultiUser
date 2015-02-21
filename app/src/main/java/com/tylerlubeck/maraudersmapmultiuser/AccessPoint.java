package com.tylerlubeck.maraudersmapmultiuser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import android.support.annotation.NonNull;

/**
 * Created by brettfischlerandkennethwapman on 10/11/14.
 */
class AccessPoint implements Comparable<AccessPoint>{
    private final String location_uri;
    private final String mac_address;
    private final Date recorded;
    private String resource_uri;
    private final double signal_strength;
    private final double standard_deviation;

    /**
     * Creates an AccessPoint object
     * @param mac_address           The MAC Address of the access point
     * @param signal_strength       The average RSS of the access point
     * @param standard_deviation    The standard deviation of the strengths of the access point
     * @param location_uri          The Location associated with this access point
     */
    AccessPoint(String mac_address, double signal_strength, double standard_deviation, String location_uri) {
        this.mac_address = mac_address;
        this.signal_strength = signal_strength;
        this.standard_deviation = standard_deviation;
        this.location_uri = location_uri;
        this.recorded = new Date();
    }

    /**
     * Converts the Access Point to the JSON format expected by the server
     * @return  The JSONObject representing the Access Point
     * @throws JSONException    In case building the object fails
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject access_point = new JSONObject();
        access_point.put("mac_address", this.mac_address);
        access_point.put("signal_strength", this.signal_strength);
        access_point.put("standard_deviation", this.standard_deviation);
        access_point.put("recorded", DateUtilities.getISO8601StringForDate(this.recorded));
        access_point.put("location", location_uri);
        return access_point;
    }

    /**
     * @returns the mac address of the access point
     */
    String getMacAddress()
    {
        return this.mac_address;
    }

    /**
     * @returns the average RSS of the access point
     */
    double getSignalStrength()
    {
        return this.signal_strength;
    }

    /**
     * Compare Access Points so that they can be sorted in a list
     * @param accessPoint the access point to compare to
     * @return  The result of the comparison
     */
    @Override
    @NonNull
    public int compareTo(AccessPoint accessPoint) {
        return Double.compare(this.signal_strength,
                              accessPoint.getSignalStrength());
    }

    /**
     * @returns A String representation of the Access Point
     */
    @Override
    public String toString() {
        return String.format("%s: %f", this.mac_address, this.signal_strength);
    }
}
