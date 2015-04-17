package com.tylerlubeck.maraudersmapmultiuser.API;

import com.tylerlubeck.maraudersmapmultiuser.Models.FacebookFriend;
import com.tylerlubeck.maraudersmapmultiuser.Models.LocateMeBody;
import com.tylerlubeck.maraudersmapmultiuser.Models.MyLocation;
import com.tylerlubeck.maraudersmapmultiuser.Models.RespondToRequestBody;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Tyler on 4/16/2015.
 */
public interface PositioningService {
    @POST("/api/v1/user_location/friend/")
    void findFriend(@Body FacebookFriend body, Callback<Object> callback);

    @POST("/api/v1/user_location/respond/")
    void respondToRequest(@Body RespondToRequestBody body, Callback<Object> callback);

    @POST("/api/v1/user_location/me/")
    void locateMe(@Body LocateMeBody body, Callback<MyLocation> callback);

}
