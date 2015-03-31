package com.tylerlubeck.maraudersmapmultiuser;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.apache.http.client.HttpResponseException;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Tyler on 10/28/2014.
 */

/**
 * An implementation of the GenericGETTask class.
 *  This implementation is to retrieve Floor objects from the server
 */
class PostMyLocationAsyncTask extends GenericPOSTAPIKeyTask {


    private final FloorMapImage floor_image;
    private final Context context;
    /**
     * Constructs a GET request to
     * @param _url  The URL to to query
     * @param seen_access_points   The access points seen
     */
    public PostMyLocationAsyncTask(String _url, JSONObject seen_access_points, FloorMapImage _floor_image,
                                   String username, String password, Context context) {
        super(_url, seen_access_points, username, password);
        this.floor_image = _floor_image;
        this.context = context;
    }


    /**
     * Process the data returned by the server. In this case, it contains all of the information
     *      necessary to pick the correct image and plot the user's location on it.
     *      So, you know, do that.
     * @param response The HTTPResponse returned by the POST
     */
    @Override
    void processResponse(String response) {
        /* TODO: Check for 200 response */
        try {
            JSONObject json_response = new JSONObject(response);
            int x_coordinate = json_response.getInt("x_coordinate");
            int y_coordinate = json_response.getInt("y_coordinate");
            int floor_number = json_response.getInt("floor_number");
            String building_name = json_response.getString("building_name");
            String image_url = json_response.getString("image_url");
            this.floor_image.setImageFromUrl(image_url, x_coordinate, y_coordinate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    void handleResponseException(HttpResponseException responseException) {
        Log.d("BUILDINGMAPPER", String.format("%d: %s", responseException.getStatusCode(), responseException.getMessage()));
        responseException.printStackTrace();
        Crashlytics.logException(responseException);
    }

    /**
     * Makes a notifaction with title "title" and content "text"
     * @param title The Title of the notification
     * @param text The content of the notification
     */
    void makeNotification(String title, String text){
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this.context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher);

        Notification notification = builder.getNotification();
        notificationManager.notify(20, notification);
    }
}
