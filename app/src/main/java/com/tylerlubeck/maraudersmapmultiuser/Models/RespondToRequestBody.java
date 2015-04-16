package com.tylerlubeck.maraudersmapmultiuser.Models;

import java.util.List;

/**
 * Created by Tyler on 4/16/2015.
 */
public class RespondToRequestBody {
    boolean allow_request;
    int requestor_id;
    List<AccessPoint> access_points;
}
