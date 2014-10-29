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
public class GetFloorsAsyncTask extends GenericGETTask<Floor> {
    public GetFloorsAsyncTask(String _url, ArrayAdapter _adapter, List<BasicNameValuePair> _params) {
        super(_url, _adapter, _params);
    }

    @Override
    ArrayList<Floor> processData(String data) throws JSONException {
        ArrayList<Floor> floor_list = new ArrayList<Floor>();
        JSONArray floors = new JSONArray(data);
        int num_floors = floors.length();
        for(int i = 0; i < num_floors; i++) {
            Floor floor = new Floor(floors.getJSONObject(i));
            floor_list.add(floor);
            Log.d("MAPBUILDER", floor.toString());
        }
        return floor_list;
    }

    public void execute() {
        super.execute();
    }
}
