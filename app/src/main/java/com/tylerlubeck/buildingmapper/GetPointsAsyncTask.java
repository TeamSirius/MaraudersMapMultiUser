package com.tylerlubeck.buildingmapper;

import android.util.Log;
import android.widget.ArrayAdapter;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 10/28/2014.
 */
public class GetPointsAsyncTask extends FillDropDownAsyncTask<String> {
    public GetPointsAsyncTask(String _url, ArrayAdapter _adapter, List<BasicNameValuePair> _params) {
        super(_url, _adapter, _params);
    }

    @Override
    ArrayList<String> processData(String data) throws JSONException {
        ArrayList<String> point_list = new ArrayList<String>();
        JSONArray points = new JSONArray(data);
        int num_points = points.length();
        for(int i = 0; i < num_points; i++) {
            String point = points.getString(i);
            point_list.add(point);
            Log.d("MAPBUILDER", point);
        }
        return point_list;
    }

    public void execute() {
        super.execute();
    }
}
