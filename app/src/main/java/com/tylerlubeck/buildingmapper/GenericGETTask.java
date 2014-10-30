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
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tyler on 10/16/2014.
 */
public abstract class GenericGETTask<T> extends AsyncTask<Void, Void, ArrayList<T>> {

    abstract ArrayList<T> processData(String data) throws JSONException;

    private final String url;
    private final ArrayAdapter adapter;
    private final List<BasicNameValuePair> params;

    public GenericGETTask(String _url, ArrayAdapter _adapter, List<BasicNameValuePair> _params) {
        this.adapter = _adapter;
        this.params = _params;
        Uri.Builder uri_builder = Uri.parse(_url).buildUpon();
        if(this.params != null) {
            for (BasicNameValuePair pair : this.params) {
                uri_builder.appendQueryParameter(pair.getName(), pair.getValue());
            }
        }
        this.url = uri_builder.build().toString();
        Log.d("BUILDINGMAPPER", this.url);


    }

    @Override
    protected ArrayList<T> doInBackground(Void... voids) {
        String response;
        ArrayList<T> response_list = new ArrayList<T>();
        try {
            HttpClient http_client = new DefaultHttpClient();
            HttpGet http_get = new HttpGet(this.url);
            HttpResponse http_response = http_client.execute(http_get);
            HttpEntity http_entity = http_response.getEntity();
            response = EntityUtils.toString(http_entity);
            response_list = processData(response);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response_list;
    }

    @Override
    protected void onPostExecute(ArrayList<T> entries) {
        this.adapter.clear();
        this.adapter.addAll(entries);
        this.adapter.notifyDataSetChanged();
    }
}
