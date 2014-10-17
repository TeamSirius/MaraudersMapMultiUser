package com.tylerlubeck.buildingmapper;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tyler on 10/16/2014.
 */
public class FillDropDownAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {

    private final String url;
    private final ArrayAdapter adapter;

    public FillDropDownAsyncTask(String _url, ArrayAdapter _adapter) {
        this.url = _url;
        this.adapter = _adapter;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        String response;
        ArrayList<String> response_list = new ArrayList<String>();
        try {
            HttpClient http_client = new DefaultHttpClient();
            HttpGet http_get = new HttpGet(this.url);
            HttpResponse http_response = http_client.execute(http_get);
            HttpEntity http_entity = http_response.getEntity();
            response = EntityUtils.toString(http_entity);
            Log.d("BUILDINGMAPPER", "Response is: " + response);
            JSONArray json_array = new JSONArray(response);

            int length = json_array.length();
            for(int i = 0; i < length; i++) {
                response_list.add(json_array.getString(i));
            }

        } catch (JSONException e) {
            Log.d("BUILDINGMAPPER", "JSON Exception!");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: Remove these lines
        response_list.add("DUMMY");
        response_list.add("DUMMY TWO");
        return response_list;
    }

    @Override
    protected void onPostExecute(ArrayList<String> entries) {
        Log.d("BUILDINGMAPPER", "SUCCESS");

        for (String s : entries) {
            Log.d("BUILDINGMAPPER", s);
        }

        this.adapter.clear();
        this.adapter.addAll(entries);
        this.adapter.notifyDataSetChanged();
    }
}
