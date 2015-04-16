package com.tylerlubeck.maraudersmapmultiuser.Tasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;


import com.tylerlubeck.maraudersmapmultiuser.Models.FloorMapImage;
import com.tylerlubeck.maraudersmapmultiuser.R;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

/**
 * Created by Tyler on 10/29/2014.
 */

/**
 * A class to post a list of access points to the server, associated with a certain location.
 * Why PATCH? See here:
 * http://django-tastypie.readthedocs.org/en/latest/interacting.html#bulk-operations
 */
public class PostAccessPointsTask extends GenericPATCHTask {

    private final Context context;
    private final FloorMapImage floor_image;

    /**
     *
     * @param _url  The URL to POST to
     * @param _data The data to POST
     * @param _context The context to make Toasts in
     * @param _floor_image  The Image to draw the located point on
     */
    public PostAccessPointsTask(String _url, JSONObject _data, Context _context, FloorMapImage _floor_image,
                                String username, String password) {
        super(_url, _data, username, password);
        this.floor_image = _floor_image;
        this.context = _context;
    }

    /**
     * Process the response.
     *     If the response is not an error, then grab the x and y coordinates and plot them on the image.
     * @param response The HTTPResponse returned by the POST
     */
    @Override
    void processData(HttpResponse response) {
        String status_line = response.getStatusLine().toString();
        this.makeToast(status_line);
        this.makeNotification("GOT A RESPONSE", status_line);
    }

    /**
     * Makes a notifaction with title "title" and content "text"
     * @param title The Title of the notification
     * @param text The content of the notification
     */
    void makeNotification(String title, String text){
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

        long pattern[] = {500, 500, 500};
        Notification.Builder builder = new Notification.Builder(this.context)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setVibrate(pattern)
                        .setSmallIcon(R.drawable.ic_launcher);

        Notification notification = builder.getNotification();
        notificationManager.notify(20, notification);
    }

    /**
     * Makes and shows a Toast message for a Toast.LENGTH_LONG amount of time
     * @param msg   The message to display
     */
    private void makeToast(String msg) {
        Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show();
    }
}
