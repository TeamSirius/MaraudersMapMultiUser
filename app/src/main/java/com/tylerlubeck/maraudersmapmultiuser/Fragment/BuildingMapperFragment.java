package com.tylerlubeck.maraudersmapmultiuser.Fragment;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.tylerlubeck.maraudersmapmultiuser.API.PositioningService;
import com.tylerlubeck.maraudersmapmultiuser.API.RequestInterceptors;
import com.tylerlubeck.maraudersmapmultiuser.AccessPointManager;
import com.tylerlubeck.maraudersmapmultiuser.Activities.MainActivity;
import com.tylerlubeck.maraudersmapmultiuser.Models.AccessPoint;
import com.tylerlubeck.maraudersmapmultiuser.Models.FloorMapImage;
import com.tylerlubeck.maraudersmapmultiuser.Models.LocateMeBody;
import com.tylerlubeck.maraudersmapmultiuser.Models.MyLocation;
import com.tylerlubeck.maraudersmapmultiuser.R;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

import java.util.ArrayList;


public class BuildingMapperFragment extends Fragment implements View.OnClickListener{

    private FloorMapImage floor_image;
    private Button update_map;
    private ImageView imageView;
    private TextView loadingText;
    private ProgressBar progress;
    private TextView we_found_you;
    private TextView user_location;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.show_location_layout,
                container,
                false);

        /* Get layout views */
        this.imageView = (ImageView) relativeLayout.findViewById(R.id.floorImage);
        this.floor_image = new FloorMapImage(imageView, this.getActivity());
        this.loadingText = (TextView) relativeLayout.findViewById(R.id.map_loading_textview);
        this.update_map = (Button) relativeLayout.findViewById(R.id.update_map_btn);
        this.progress = (ProgressBar) relativeLayout.findViewById(R.id.map_loading_spinner);
        this.we_found_you = (TextView) relativeLayout.findViewById(R.id.we_found_you);
        this.user_location = (TextView) relativeLayout.findViewById(R.id.user_location);
        this.update_map.setOnClickListener(this); // The user has asked to update the map


        /*
         * If this has been launched because somebody sent me their location,
         * then just display their location.
         *
         * We'll know someone requested my location if this Fragment has been
         *     started with arguments
         */

        Bundle args = this.getArguments();
        if (args == null) {
            this.getLocation();
        } else {
            this.displayFriendsLocation(args);
        }
        return relativeLayout;
    }


    /**
     * If the wifi is not currently enabled, then enable it.
     * Return true if wifi was enabled prior to this method being called
     */
    private boolean ensureWifiEnabled() {
        WifiManager wifi = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
        if (! wifi.isWifiEnabled() ) {
            wifi.setWifiEnabled(true);
            return false;
        }
        return true;
    }

    private void disableWifi() {
        WifiManager wifi = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
        //wifi.setWifiEnabled(false);
    }

    private void displayFriendsLocation(Bundle args) {
        Log.d("MARAUDERSMAP", "Showing your friend!");
        String image_url = args.getString("image_url");
        int x_coordinate = args.getInt("x_coordinate");
        int y_coordinate = args.getInt("y_coordinate");
        this.we_found_you.setText(args.getString("friend_name"));
        this.we_found_you.setVisibility(View.VISIBLE);
        this.floor_image.setImageFromUrl(image_url, x_coordinate, y_coordinate);
    }

    private PositioningService getPositioningService() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String apiKey = preferences.getString("api_key", "");
        String facebookUsername = preferences.getString("facebook_username", "");
        RestAdapter restAdapter  = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.root_server))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog(MainActivity.LOG_TAG))
                .setRequestInterceptor(new RequestInterceptors.AuthorizedHeaderIntercepter(facebookUsername, apiKey))
                .build();

        return restAdapter.create(PositioningService.class);
    }

    /**
     * Initiates the action that scans for the user's location
     */
    private void getLocation() {
        Log.d("MARAUDERSMAP", "Looking you up!");
        final boolean wifiWasEnabled = this.ensureWifiEnabled();

        /* If trying to locate yourself, do two scans and upload it */
        new AccessPointManager(this.getActivity()) {
            @Override
            protected void allDataReceived(ArrayList<AccessPoint> accessPointData) {
                PositioningService positioningService = getPositioningService();
                LocateMeBody locateMeBody = new LocateMeBody(accessPointData);
                positioningService.locateMe(locateMeBody, new Callback<MyLocation>() {
                    @Override
                    public void success(MyLocation myLocation, Response response) {
                        displayLocation(myLocation);
                        if (wifiWasEnabled) {
                            disableWifi();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        displayError();
                        Log.e(MainActivity.LOG_TAG, "Failed to get location");
                        Crashlytics.logException(error);
                        if (wifiWasEnabled) {
                            disableWifi();
                        }
                    }
                });
            }
        };
    }

    private void displayLocation(MyLocation location) {
        this.floor_image.setImageFromUrl(location.getImageUrl(),
                                         location.getXCoordinate(),
                                         location.getYCoordinate());
        this.we_found_you.setVisibility(View.VISIBLE);
        this.user_location.setVisibility(View.VISIBLE);
        this.user_location.setText(location.getFloorName());

    }

    private void displayError() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_map_btn:
                this.imageView.setVisibility(View.GONE);
                this.update_map.setVisibility(View.GONE);
                this.we_found_you.setVisibility(View.GONE);
                this.user_location.setVisibility(View.GONE);
                this.loadingText.setVisibility(View.VISIBLE);
                this.progress.setVisibility(View.VISIBLE);
                this.loadingText.setText("Looking you up...");
                this.getLocation();
                break;
        }
    }
}
