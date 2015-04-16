package com.tylerlubeck.maraudersmapmultiuser.Tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
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
public abstract class GenericRespondToRequestTask extends AsyncTask<Void, Void, HttpEntity> {
    private enum Type {
        OBJECT,
        ARRAY
    }

    /**
     * An abstract function to allow for processing the data returned.
     * @param response The response string from the request
     */
    abstract void processResponse(HttpEntity entity);

    /**
     * Allow the handling of an exception.
     * @param responseException     The exception to handle
     */
    abstract void handleResponseException(int statusCode, HttpEntity entity);

    private final String url;
    private JSONArray array_data;
    private JSONObject object_data;
    private Type type;
    private int statusCode;
    private String username;
    private String password;

    private GenericRespondToRequestTask(String _url, String username, String password) {
        this.username = username;
        this.password = password;

        Uri.Builder uri_builder = Uri.parse(_url).buildUpon();


        this.url = uri_builder.build().toString();
    }

    /**
     *
     * @param _url      The URL to POST to
     * @param _array_data     The JSON data to POST
     */
    public GenericRespondToRequestTask(String _url, JSONArray _array_data, String username, String password) {
        this(_url, username, password);
        this.array_data = _array_data;
        this.type = Type.ARRAY;

        Log.d("BUILDINGMAPPER", String.format("POSTing to %s", this.url));
    }

    /**
     *
     * @param _url      The URL to POST to
     * @param _array_data     The JSON data to POST
     */
    GenericRespondToRequestTask(String _url, JSONObject _array_data, String username, String password) {
        this(_url, username, password);
        this.object_data = _array_data;
        this.type = Type.OBJECT;
    }

    /**
     *
     * @param voids because it needs no parameters
     * @return the HttpResponse from the POST query
     */
    @Override
    protected HttpEntity doInBackground(Void... voids) {
        String response_string = "";
        HttpEntity entity = null;
        try {
            HttpParams http_params = new BasicHttpParams();
            HttpClient http_client = new DefaultHttpClient(http_params);
            HttpPost http_post = new HttpPost(this.url);
            if (this.type == Type.ARRAY) {
                http_post.setEntity(new ByteArrayEntity(this.array_data.toString().getBytes("UTF8")));
            } else if (this.type == Type.OBJECT) {
                http_post.setEntity(new ByteArrayEntity(this.object_data.toString().getBytes("UTF8")));
            }
            http_post.setHeader("Accept", "application/json");
            http_post.setHeader("Content-type", "application/json");
            http_post.setHeader("Authorization", String.format("ApiKey %s:%s", this.username, this.password));

            HttpResponse http_response = http_client.execute(http_post);
            this.statusCode = http_response.getStatusLine().getStatusCode();
            entity = http_response.getEntity();

        } catch (ClientProtocolException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * Runs the processResponse function after the hard work has finished
     * @param response_string   If there was an exception with the request, hand off to the
     *                              exception handler. Otherwise, process the response.
     */
    @Override
    protected void onPostExecute(HttpEntity entity) {
        if (this.statusCode >= 300) {
            this.handleResponseException(this.statusCode, entity);
        } else {
            this.processResponse(entity);
        }
    }
}
