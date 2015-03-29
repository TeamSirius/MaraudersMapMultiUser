package com.tylerlubeck.maraudersmapmultiuser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tyler on 10/16/2014.
 */

/**
 * Implements a Generic Asynchronous HTTP POST
 */
public abstract class GenericPOSTAPIKeyTask extends AsyncTask<Void, Void, String> {
    private enum Type {
        OBJECT,
        ARRAY
    }

    /**
     * An abstract function to allow for processing the data returned.
     * @param response The response string from the request
     */
    abstract void processResponse(String response);

    /**
     * Allow the handling of an exception.
     * @param responseException     The exception to handle
     */
    abstract void handleResponseException(HttpResponseException responseException);

    private final String url;
    private JSONArray array_data;
    private JSONObject object_data;
    private Type type;
    private HttpResponseException responseException;
    private String username;
    private String password;

    private GenericPOSTAPIKeyTask(String _url, String username, String password) {
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
    public GenericPOSTAPIKeyTask(String _url, JSONArray _array_data, String username, String password) {
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
    GenericPOSTAPIKeyTask(String _url, JSONObject _array_data, String username, String password) {
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
    protected String doInBackground(Void... voids) {
        HttpResponse http_response;
        String response_string = "";
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

            http_response = http_client.execute(http_post);
            response_string = new BasicResponseHandler().handleResponse(http_response);

        } catch (HttpResponseException e) {
            this.responseException = e;
        } catch (ClientProtocolException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response_string;
    }

    /**
     * Runs the processResponse function after the hard work has finished
     * @param response_string   If there was an exception with the request, hand off to the
     *                              exception handler. Otherwise, process the response.
     */
    @Override
    protected void onPostExecute(String response_string) {
        if (this.responseException != null) {
            this.handleResponseException(this.responseException);
        } else {
            this.processResponse(response_string);
        }
    }
}
