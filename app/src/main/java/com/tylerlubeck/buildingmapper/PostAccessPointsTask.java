package com.tylerlubeck.buildingmapper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

/**
 * Created by Tyler on 10/29/2014.
 */
public class PostAccessPointsTask extends GenericPOSTTask {

    Context ctx;
    public PostAccessPointsTask(String _url, JSONObject _data, Context _ctx) {
        super(_url, _data);
        this.ctx = _ctx;
    }

    @Override
    void processData(HttpResponse response) {
        Toast.makeText(this.ctx, response.getStatusLine().toString(), Toast.LENGTH_LONG).show();
        Log.d("BUILDINGMAPPER", "SENT REQUEST");
        //Toast.makeText(this.ctx, "POSTED", Toast.LENGTH_LONG).show();
    }
}
