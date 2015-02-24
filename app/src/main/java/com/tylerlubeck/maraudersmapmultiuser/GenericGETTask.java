package com.tylerlubeck.maraudersmapmultiuser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.List;

/**
 * Created by Tyler on 10/16/2014.
 */

/**
 * Implements a Generic Asynchronous HTTP GET.
 */
abstract class GenericGETTask extends AsyncTask<Void, Void, String> {

    /**
     * Process the successful response from the server
     * @param response     The String received by the GET method
     */
    abstract void processResponse(String response);

    /**
     * Handle an exception from the server
     * @param responseException The Exception that occurred
     */
    abstract void handleResponseException(HttpResponseException responseException);

    private final String url;
    private HttpResponseException responseException;

    /**
     *
     * @param _url              The URL to GET from
     * @param _parameters       The Parameters to pass along with the GET request
     */
    GenericGETTask(String _url, List<BasicNameValuePair> _parameters) {
        Uri.Builder uri_builder = Uri.parse(_url).buildUpon();
        if(_parameters != null) {
            for (BasicNameValuePair pair : _parameters) {
                uri_builder.appendQueryParameter(pair.getName(), pair.getValue());
            }
        }
        this.url = uri_builder.build().toString();
        Log.d("BUILDINGMAPPER", this.url);
        Crashlytics.log(this.url);
    }


    /**
     * If everything works, return the response string. Otherwise, set the responseException.
     * @param voids     Because we don't need any arguments
     * @return the string containing the content from the HttpResponse
     */
    @Override
    protected String doInBackground(Void... voids) {
        String response_string = "";
        try {
            HttpClient http_client = new DefaultHttpClient();
            HttpGet http_get = new HttpGet(this.url);
            HttpResponse response = http_client.execute(http_get);
            response_string = new BasicResponseHandler().handleResponse(response);
        } catch (HttpResponseException e) {
            this.responseException = e;
        } catch (ClientProtocolException e) {
            Crashlytics.logException(e);
        } catch (IOException e) {
            Crashlytics.logException(e);
        }

        if (response_string.isEmpty()) {
            Crashlytics.log(String.format("Got a null response at address %s", this.url));
        }
        return response_string;
    }

    /**
     * If there was a successful response, hand it off to the processing function.
     * Otherwise, hand the ResponseException off to the exception handler
     * @param response_string   The String containing the content from the HttpResponse
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
