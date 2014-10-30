package com.tylerlubeck.buildingmapper;

import android.util.Log;
import android.widget.ArrayAdapter;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 10/28/2014.
 */
public class GetPointsAsyncTask extends GenericGETTask<Location> {
    public GetPointsAsyncTask(String _url, ArrayAdapter _adapter, List<BasicNameValuePair> _params) {
        super(_url, _adapter, _params);
    }

    @Override
    ArrayList<Location> processData(String data) throws JSONException {
        ArrayList<Location> point_list = new ArrayList<Location>();
        JSONArray points = new JSONArray(data);
        int num_points = points.length();
        for(int i = 0; i < num_points; i++) {
            JSONObject location_object = points.getJSONObject(i);
            Location loc = new Location(location_object);
            point_list.add(loc);
            Log.d("MAPBUILDER", loc.toString());
        }
        return point_list;
    }

    public void execute() {
        super.execute();
    }
}
