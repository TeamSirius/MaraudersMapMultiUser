package com.tylerlubeck.maraudersmapmultiuser.Models;

import java.util.List;

/**
 * Created by Tyler on 4/16/2015.
 */
public class RespondToRequestBody {
    boolean allow_request;
    int requestor_id;
    List<AccessPoint> access_points;

    public RespondToRequestBody() {}
    public RespondToRequestBody(boolean allowRequest, int requestorID) {
        this.allow_request = allowRequest;
        this.requestor_id = requestorID;
    }

    public void setAccessPoints(List<AccessPoint> accessPoints) {
        this.access_points = accessPoints;
    }
}
