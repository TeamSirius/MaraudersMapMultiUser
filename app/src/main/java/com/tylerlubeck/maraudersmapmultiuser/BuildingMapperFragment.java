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


public class BuildingMapperFragment extends Fragment implements View.OnClickListener {

    private Spinner point_picker;
    private FloorMapImage floor_image;
    private RelativeLayout relativeLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fabric.with(this.getActivity(), new Crashlytics());
        this.relativeLayout = (RelativeLayout) inflater.inflate(R.layout.activity_building_mapper,
                container,
                false);

        /* Get references to various UI elements */
        Button whereAmI = (Button) this.relativeLayout.findViewById(R.id.where_am_i_btn);
        Button findAFriend = (Button) this.relativeLayout.findViewById(R.id.request_a_location);

        whereAmI.setOnClickListener(this);
        findAFriend.setOnClickListener(this);
        return this.relativeLayout;
    }

    /**
     * Determines if the wifi is currently enabled
     *
     * @return True if the wifi is enabled, else False
     */
    boolean isWifiEnabled() {
        WifiManager wifi = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    /**
     * Handles the onClick method for all of the views in this context
     *
     * @param view The view that was clicked
     */
    @Override
    public void onClick(View view) {
        if (!isWifiEnabled()) {
            Toast.makeText(this.getActivity(), "Turn your wifi on, Dan", Toast.LENGTH_LONG).show();
            return;
        }



        /* Depending which button is clicked, do different things... */
        switch (view.getId()) {
            case R.id.where_am_i_btn:
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
                break;
            case R.id.request_a_location:
                SelectFriendFragment friendsFragment = new SelectFriendFragment();
                friendsFragment.setArguments(getActivity().getIntent().getExtras());
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, friendsFragment)
                        .addToBackStack(null)
                        .commit();
                break;

        }
    }
}
