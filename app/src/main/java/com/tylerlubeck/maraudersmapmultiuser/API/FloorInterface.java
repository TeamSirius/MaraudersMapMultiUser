package com.tylerlubeck.maraudersmapmultiuser.API;

import com.tylerlubeck.maraudersmapmultiuser.Models.Floor;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Tyler on 4/16/2015.
 */
public interface FloorInterface {
    @GET("/api/v1/floor/")
    void getFloors(Callback<List<Floor>> callback);

    @GET("/api/v1/floor/{id}")
    void getFloorById(@Path("id") int id, Callback<Floor> callback);

}
