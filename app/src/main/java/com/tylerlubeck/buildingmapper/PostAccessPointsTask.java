package com.tylerlubeck.buildingmapper;

import android.app.Notification;
import android.app.NotificationManager;
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
        if(Fimage != null){
            try {
                String jsonString = EntityUtils.toString(response.getEntity());
                JSONObject responseJson = new JSONObject(jsonString);
                Log.d("JSON RESPONSE:", responseJson.toString());
                if(!responseJson.has("error")) {
                    int x = responseJson.getInt("x");
                    int y = responseJson.getInt("y");
                    Fimage.draw_point_noclear(x, y);
                    Log.d("BUILDINGMAPPER", "UPLOADED DATA SUCCESS");
                    Toast.makeText(this.ctx, "UPLOADED DATA SUCCESS", Toast.LENGTH_LONG).show();
                    makeNotification("Map Done", "Success");
                } else {
                    Log.d("BUILDINGMAPPER", "UPLOADED DATA FAIL");
                    Toast.makeText(this.ctx, "UPLOADED DATA FAIL ACTUALLY", Toast.LENGTH_LONG).show();
                    makeNotification("Map Done, Failed", "Awwww, shit");
                }
            } catch (JSONException e) {
                Log.d("BUILDINGMAPPER", "UPLOADED DATA WORKED, BUT BAD JSON RECEIVED");
                Toast.makeText(this.ctx, "UPLOADED DATA WORKED, BUT BAD JSON", Toast.LENGTH_LONG).show();
                makeNotification("Map Done", "Bad json, nbd");
            }catch (IOException e){
                Log.d("BUILDINGMAPPER", "UPLOADED DATA FAIL: IOEXCEPTION");
                Toast.makeText(this.ctx, "UPLOADED DATA FAIL", Toast.LENGTH_LONG).show();
                makeNotification("Map Done, Failed", "Awwww, shit");
            }
        }
        //Toast.makeText(this.ctx, "POSTED", Toast.LENGTH_LONG).show();
    }

    void makeNotification(String title, String text){
        NotificationManager notificationManager = (NotificationManager) this.ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        long pattern[] = {500, 500, 500};
        Notification.Builder builder = new Notification.Builder(this.ctx)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setVibrate(pattern)
                        .setSmallIcon(R.drawable.ic_launcher);

        Notification notification = builder.getNotification();
        notificationManager.notify(20, notification);
        Log.d("BUILDINGMAPPER", "Showed notification");

    }
}
