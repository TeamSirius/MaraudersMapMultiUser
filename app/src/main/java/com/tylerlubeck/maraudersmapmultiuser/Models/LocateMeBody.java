package com.tylerlubeck.maraudersmapmultiuser.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Tyler on 4/16/2015.
 */
public class LocateMeBody {

    @SerializedName("access_points")
    List<AccessPoint> access_points;

    public LocateMeBody(){}

    public LocateMeBody(List<AccessPoint> accessPoints) {
        this.access_points = accessPoints;
    }
}
