package com.tylerlubeck.maraudersmapmultiuser;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.plus.model.people.Person;

import io.fabric.sdk.android.Fabric;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BuildingMapperFragment extends Fragment implements View.OnClickListener{

    private FloorMapImage floor_image;
    Button update_map;
    ImageView imageView;
    TextView loadingText;
    ProgressBar progress;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fabric.with(this.getActivity(), new Crashlytics());
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.activity_building_mapper,
                container,
                false);
        this.imageView = (ImageView) relativeLayout.findViewById(R.id.floorImage);
        this.floor_image = new FloorMapImage(imageView, this.getActivity());
        this.loadingText = (TextView) relativeLayout.findViewById(R.id.map_loading_textview);
        this.update_map = (Button) relativeLayout.findViewById(R.id.update_map_btn);
        this.progress = (ProgressBar) relativeLayout.findViewById(R.id.map_loading_spinner);
        this.update_map.setOnClickListener(this);

        this.getLocation();
        return relativeLayout;
    }

    /**
     * Determines if the wifi is currently enabled
     *
     * @return True if the wifi is enabled, else False
     */
    private boolean isWifiEnabled() {
        WifiManager wifi = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    /**
     * If the wifi is not currently enabled, then enable it.
     */
    private void ensureWifiEnabled() {
        WifiManager wifi = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
        if (! wifi.isWifiEnabled() ) {
            wifi.setWifiEnabled(true);
        }
    }


    /**
     * Initiates the action that scans for the user's location
     */
    private void getLocation() {
        this.ensureWifiEnabled();
                /* If trying to locate yourself, do two scans and upload it */
        new AccessPointManager(this.getActivity()) {
            @Override
            protected void allDataReceived(JSONArray accessPointData) {
                String url = getActivity().getString(R.string.locate_me_endpoint);
                JSONObject data = new JSONObject();
                try {
                    Log.e("MARAUDERSMAP", "Posting my location: ");
                    data.put("access_points", accessPointData);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String api_key = preferences.getString("api_key", "");
                    String facebook_username = preferences.getString("facebook_username", "");
                    PostMyLocationAsyncTask postMyLocationAsyncTask = new PostMyLocationAsyncTask(url,
                            data,
                            floor_image,
                            facebook_username,
                            api_key,
                            getActivity());
                    postMyLocationAsyncTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_map_btn:
                this.imageView.setVisibility(View.GONE);
                this.update_map.setVisibility(View.GONE);
                this.loadingText.setVisibility(View.VISIBLE);
                this.progress.setVisibility(View.VISIBLE);
                this.loadingText.setText("Looking you up...");
                this.getLocation();
                break;
        }
    }
}
