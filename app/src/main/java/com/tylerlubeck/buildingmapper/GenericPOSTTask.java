package com.tylerlubeck.buildingmapper;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 10/16/2014.
 */
public abstract class GenericPOSTTask extends AsyncTask<Void, Void, HttpResponse> {

    abstract void processData(HttpResponse response);

    private final String url;
    JSONObject data;

    public GenericPOSTTask(String _url, JSONObject _data) {
        this.url = _url;
        this.data = _data;
        Log.d("BUILDINGMAPPER", this.url);
    }

    @Override
    protected HttpResponse doInBackground(Void... voids) {
        HttpResponse response = null;
        try {
            HttpParams http_params = new BasicHttpParams();
            HttpClient client = new DefaultHttpClient(http_params);
            HttpPost request = new HttpPost(this.url);
            request.setEntity(new ByteArrayEntity(this.data.toString().getBytes("UTF8")));
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            response = client.execute(request);
            return response;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(HttpResponse response) {
        processData(response);
    }
}
