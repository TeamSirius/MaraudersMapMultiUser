package com.tylerlubeck.buildingmapper;

import android.widget.ArrayAdapter;
import com.crashlytics.android.Crashlytics;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 10/28/2014.
 */

/**
 * An implementation of the GenericGETTask class.
 *  This implementation is to retrieve Location objects from the server
 */
class GetPointsAsyncTask extends GenericGETTask {

    private final ArrayAdapter adapter;

    /**
     *
     * @param _url The URL to query
     * @param _adapter The ArrayAdapter to fill after a successful query
     * @param _params  The querystring parameters
     */
    public GetPointsAsyncTask(String _url, ArrayAdapter _adapter, List<BasicNameValuePair> _params) {
        super(_url, _params);
        this.adapter = _adapter;
    }

    /**
     * If the response was not valid JSON, log the exception. Otherwise process it.
     * @param response     The String received by the GET method
     */
    @Override
    void processResponse(String response) {

        try {
            this.processData(response);
        } catch (JSONException e) {
            Crashlytics.logException(e);
        }
    }

    /**
     * Handle an exception that occurred during the HTTP Request
     * @param e the exception that occurred
     */
    @Override
    void handleResponseException(HttpResponseException e) {
        Crashlytics.logException(e);
    }

    /**
     * Process the data returned by the server
     * @param response          The String content of the response
     * @throws JSONException    In case the response is not valid JSON
     */
    private void processData(String response) throws JSONException {
        /* TODO: Check for 200 response */

        ArrayList<Location> point_list = new ArrayList<Location>();
        JSONObject response_data = new JSONObject(response);
        JSONArray points = response_data.getJSONArray("objects");
        int points_length = points.length();
        for(int i = 0; i < points_length; i++) {
            Location location = new Location(points.getJSONObject(i));
            point_list.add(location);
        }

        this.adapter.clear();
        this.adapter.addAll(point_list);
        this.adapter.notifyDataSetChanged();
    }
}
