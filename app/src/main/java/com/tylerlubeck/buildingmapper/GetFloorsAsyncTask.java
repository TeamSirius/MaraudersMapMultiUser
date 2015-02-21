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
 *  This implementation is to retrieve Floor objects from the server
 */
class GetFloorsAsyncTask extends GenericGETTask {

    private final ArrayAdapter adapter;

    /**
     * Constructs an Asynchronous GET request to retrieve the list of Floors from the server.
     * @param _url  The URL to to query
     * @param _adapter  The ArrayAdapter to fill after processing
     * @param _params   Parameters to pass with the query
     */
    public GetFloorsAsyncTask(String _url, ArrayAdapter _adapter, List<BasicNameValuePair> _params) {
        super(_url, _params);
        this.adapter = _adapter;
    }

    /**
     * Constructs an Asynchronous GET request to retrieve the list of Floors from the server.
     *      Does not require any query parameters.
     * @param _url  The URL to to query
     * @param _adapter  The ArrayAdapter to fill after processing
     */
    public GetFloorsAsyncTask(String _url, ArrayAdapter _adapter) {
        super(_url, null /* No query parameters */);
        this.adapter = _adapter;
    }

    /**
     * On a basic level, delegates to the processData function.
     * More importantly, it handles any errors that the processData function may throw.
     * @param response_string     The String received by the GET method
     */
    @Override
    void processResponse(String response_string) {
        try{
            this.processData(response_string);
        } catch (JSONException e) {
            Crashlytics.logException(e);
        }
    }

    /**
     * In case we want to do something with the exception later, like display an error,
     *      we can build that in to this code.
     * @param e The HttpResponseException to do something with
     */
    @Override
    void handleResponseException(HttpResponseException e) {
        Crashlytics.logException(e);
    }

    /**
     * Convert the response_string to a JSON Object and then process the
     *      object in to a list of floor items. Then, add all of the floors to the
     *      adapter so that they can be displayed.
     * @param response_string   The response string to process
     * @throws JSONException    In case the response_string isn't valid JSON
     */
    private void processData(String response_string) throws JSONException {
        /* TODO: Check for 200 response */
        ArrayList<Floor> floor_list = new ArrayList<Floor>();
        JSONObject json_response = new JSONObject(response_string);

        JSONArray floors = json_response.getJSONArray("objects");
        int floors_length = floors.length();
        for(int i = 0; i < floors_length; i++) {
            Floor floor = new Floor(floors.getJSONObject(i));
            floor_list.add(floor);
        }

        this.adapter.clear();
        this.adapter.addAll(floor_list);
        this.adapter.notifyDataSetChanged();
    }
}
