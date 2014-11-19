package com.tylerlubeck.buildingmapper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.util.EntityUtils;
import android.util.Log;
import java.io.IOException;

/**
 * Created by Tyler on 10/29/2014.
 */
public class PostAccessPointsTask extends GenericPOSTTask {

    Context ctx;
    FloorMapImage Fimage;

    public PostAccessPointsTask(String _url, JSONObject _data, Context _ctx) {
        super(_url, _data);
        this.ctx = _ctx;
        Fimage = null;
    }

    public PostAccessPointsTask(String _url, JSONObject _data, Context _ctx, FloorMapImage _Fimage) {
        super(_url, _data);
        Fimage = _Fimage;
        this.ctx = _ctx;
    }

    @Override
    void processData(HttpResponse response) {
        Toast.makeText(this.ctx, response.getStatusLine().toString(), Toast.LENGTH_LONG).show();
        Log.d("BUILDINGMAPPER", "SENT REQUEST");
        if(Fimage != null){
            try {
                String jsonString = EntityUtils.toString(response.getEntity());
                JSONObject reponseJson = new JSONObject(jsonString);
                Log.d("JSON RESPONSE:",reponseJson.toString());
                if(!reponseJson.has("error")) {
                    int x = reponseJson.getInt("x");
                    int y = reponseJson.getInt("y");
                    Fimage.draw_point_noclear(x, y);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        //Toast.makeText(this.ctx, "POSTED", Toast.LENGTH_LONG).show();
    }
}
