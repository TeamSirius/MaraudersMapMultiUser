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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class BuildingMapperFragment extends Fragment {

    private Spinner point_picker;
    private FloorMapImage floor_image;
    private RelativeLayout relativeLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fabric.with(this.getActivity(), new Crashlytics());
        this.relativeLayout = (RelativeLayout) inflater.inflate(R.layout.activity_building_mapper,
                container,
                false);

        this.getLocation();
        return this.relativeLayout;
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
     * Initiates the action that scans for the user's location
     */
    private void getLocation() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");
                /* If trying to locate yourself, do two scans and upload it */
        Toast.makeText(this.getActivity(),
                String.format("About to do %d scans", AccessPointManager.NUM_QUERY_POLLS),
                Toast.LENGTH_LONG).show();
        new AccessPointManager(this.getActivity(),
                this.floor_image,
                username,
                password);
    }
}
