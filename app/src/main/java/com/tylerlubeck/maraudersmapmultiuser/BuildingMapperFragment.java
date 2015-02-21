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


public class BuildingMapperFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

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
        Spinner floor_picker = (Spinner) this.relativeLayout.findViewById(R.id.floor_picker_spinner);
        Button whereAmI = (Button) this.relativeLayout.findViewById(R.id.where_am_i_btn);
        Button saveBtn = (Button) this.relativeLayout.findViewById(R.id.save_access_points_btn);
        point_picker = (Spinner) this.relativeLayout.findViewById(R.id.location_picker_spinner);

        /* Create an adapter for the list of Floors, and then start the Task to fill it*/
        FloorListAdapter floor_adapter = new FloorListAdapter(this.getActivity(), new ArrayList<Floor>());
        floor_picker.setAdapter(floor_adapter);
        GetFloorsAsyncTask fill_floors_drop_down = new GetFloorsAsyncTask(getString(R.string.floor_endpoint),
                                                                          floor_adapter);
        fill_floors_drop_down.execute();

        /* Set listeners */
        floor_picker.setOnItemSelectedListener(this);
        saveBtn.setOnClickListener(this);
        whereAmI.setOnClickListener(this);
        return this.relativeLayout;
    }

    /**
     * Determines if the wifi is currently enabled
     * @return True if the wifi is enabled, else False
     */
    boolean isWifiEnabled() {
        WifiManager wifi = (WifiManager)this.getActivity().getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    /**
     * Handles the onClick method for all of the views in this context
     * @param view The view that was clicked
     */
    @Override
    public void onClick(View view) {
        Location selected_location;

        if (! isWifiEnabled()) {
            Toast.makeText(this.getActivity(), "Turn your wifi on, Dan", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");

        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this.getActivity(), "Go set your username and password", Toast.LENGTH_LONG).show();
            return;
        }

        /* Depending which button is clicked, do different things... */
        switch (view.getId()) {
            case R.id.save_access_points_btn:
                /* If doing mapping, do 30 scans and upload them */
                Toast.makeText(this.getActivity(),
                               String.format("About to do %d scans", AccessPointManager.NUM_MAPPING_POLLS),
                               Toast.LENGTH_LONG).show();
                selected_location = (Location) point_picker.getSelectedItem();

                new AccessPointManager(this.getActivity(),
                                       this.floor_image,
                                       selected_location.getResourceUri(),
                                       username,
                                       password);
                /*
                 * For convenience, make the point_picker jump ahead 4 spots after an upload.
                 * This is because we're not currently dealing with directional locations
                 */
                int num_items = point_picker.getAdapter().getCount() - 1;
                int next_pos = point_picker.getSelectedItemPosition() + 4;
                int next_index = num_items < next_pos ? num_items : next_pos;
                //point_picker.setSelection(next_index);
                break;
            case R.id.where_am_i_btn:
                /* If trying to locate yourself, do two scans and upload it */
                Toast.makeText(this.getActivity(),
                               String.format("About to do %d scans", AccessPointManager.NUM_QUERY_POLLS),
                               Toast.LENGTH_LONG).show();
                new AccessPointManager(this.getActivity(),
                                       this.floor_image,
                                       username,
                                       password);
                break;

        }
    }

    /**
     *  Handles the ItemSelected event for Spinners.
     *      Specifically, this one listens for ItemSelected events on the floor_picker.
     *      When the floor_picker's selection changes, we need to query for new locations
     * @param adapterView   The AdapterView where the selection happened
     * @param view          The view within the AdapterView that was clicked
     * @param position      The position of the view in the adapter
     * @param id            The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        Floor room = (Floor) adapterView.getItemAtPosition(position);
        ArrayAdapter<String> adapter = (ArrayAdapter) point_picker.getAdapter();
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        ArrayAdapter<String> points_adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1, new ArrayList<String>());
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("floor__building_name", room.getBuilding()));
        params.add(new BasicNameValuePair("floor__floor_number", String.valueOf(room.getFloor())));

        Log.d("BUILDINGMAPPER", String.format("Getting points for %s %s", room.getBuilding(), String.valueOf(room.getFloor())));

        ImageView image = (ImageView) this.relativeLayout.findViewById(R.id.floorImage);

        floor_image = new FloorMapImage(room.getBuilding(), room.getFloor(), image, this.getActivity());
        point_picker.setAdapter(points_adapter);
        point_picker.setOnItemSelectedListener(floor_image);


        /* For Django server, use this line: */
        GetPointsAsyncTask fill_points_drop_down = new GetPointsAsyncTask(getString(R.string.location_endpoint), points_adapter, params);

        /* For Flask server, use these two lines: */
        //String url = getString(R.string.get_access_points_url) + "/" + room.getBuilding() + "/" + String.valueOf(room.getFloor());
        //GetPointsAsyncTask fill_points_drop_down = new GetPointsAsyncTask(url, points_adapter, null);

        fill_points_drop_down.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d("BUILDINGMAPPER", "NOTHING SELECTED");
        Spinner point_picker = (Spinner) this.relativeLayout.findViewById(R.id.location_picker_spinner);
        ArrayAdapter<String> adapter = (ArrayAdapter)point_picker.getAdapter();
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}
