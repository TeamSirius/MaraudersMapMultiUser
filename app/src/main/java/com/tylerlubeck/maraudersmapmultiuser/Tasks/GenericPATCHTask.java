package com.tylerlubeck.maraudersmapmultiuser.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Tyler on 10/16/2014.
 */

/**
 * Implements a Generic Asynchronous HTTP POST
 */
public abstract class GenericPATCHTask extends AsyncTask<Void, Void, HttpResponse> {
    private enum Type {
        OBJECT,
        ARRAY
    }

    /**
     * An abstract function to allow for processing the object_data returned.
     * @param response The HTTPResponse returned by the POST
     */
    abstract void processData(HttpResponse response);

    private final String url;
    private JSONObject object_data;
    private JSONArray array_data;
    private Type type;
    private final UsernamePasswordCredentials basicAuthCredentials;

    private GenericPATCHTask(String _url, String username, String password) {
        this.url = _url;
        this.basicAuthCredentials = new UsernamePasswordCredentials(username, password);
    }
    /**
     *
     * @param _url      The URL to POST to
     * @param _object_data     The JSON OBJECT data to POST
     */
    GenericPATCHTask(String _url, JSONObject _object_data, String username, String password) {
        this(_url, username, password);
        this.object_data = _object_data;
        this.type = Type.OBJECT;

        Log.d("BUILDINGMAPPER", String.format("POSTing to %s", this.url));
    }

    /**
     *
     * @param _url      The URL to POST to
     * @param _array_data      The JSON ARRAY data to POST
     */
    public GenericPATCHTask(String _url, JSONArray _array_data, String username, String password) {
        this(_url, username, password);
        this.array_data = _array_data;
        this.type = Type.ARRAY;

        Log.d("BUILDINGMAPPER", String.format("POSTing to %s", this.url));
    }

    /**
     *
     * @param voids because it needs no parameters
     * @return the HttpResponse from the POST query
     */
    @Override
    protected HttpResponse doInBackground(Void... voids) {
        HttpResponse http_response = null;
        try {
            HttpParams http_params = new BasicHttpParams();
            HttpClient http_client = new DefaultHttpClient(http_params);
            HttpPost http_post = new HttpPost(this.url);

            if (this.type == Type.OBJECT) {
                http_post.setEntity(new ByteArrayEntity(this.object_data.toString().getBytes("UTF8")));
            } else if (this.type == Type.ARRAY) {
                http_post.setEntity(new ByteArrayEntity(this.array_data.toString().getBytes("UTF8")));
            }
            http_post.setHeader("Accept", "application/json");
            http_post.setHeader("Content-type", "application/json");
            http_post.addHeader(BasicScheme.authenticate(this.basicAuthCredentials,
                                                         "UTF-8",
                                                         false));

            /*
             * This is the part that makes it a PATCH
             * See here: http://django-tastypie.readthedocs.org/en/latest/resources.html#using-put-delete-patch-in-unsupported-places
             */
            http_post.addHeader("X-HTTP-Method-Override", "PATCH");
            http_response = http_client.execute(http_post);
            return http_response;
        } catch (ClientProtocolException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return http_response;
    }

    /**
     * Runs the processResponse function after the hard work has finished
     * @param response  The HTTPResponse returned by the POST
     */
    @Override
    protected void onPostExecute(HttpResponse response) {
        processData(response);
    }
}
