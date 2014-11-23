package com.tylerlubeck.buildingmapper;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BuildingMapper extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner point_picker;
    private Spinner room_picker;
    private Button whereAmI;
    private Button getAPs;
    private Button saveBtn;
    private FloorMapImage Fimage;

    void getAccessPoints(int numberOfPolls, boolean upload, int id) {
        new AccessPointManager(this, numberOfPolls, upload, id, this.Fimage);
    }

    boolean isWifiEnabled() {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    void postToServer(JSONArray data, int id, Context ctx) {
        JSONObject send_data = new JSONObject();
        try {
            send_data.put("APS", data);
            send_data.put("lid", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PostAccessPointsTask post_access_points = new PostAccessPointsTask(getString(R.string.post_access_points), send_data, ctx, Fimage);
        post_access_points.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context global_ctx = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_mapper);
        getAPs = (Button) findViewById(R.id.get_access_points_btn);
        whereAmI = (Button) findViewById(R.id.where_am_i_btn);
        saveBtn = (Button) findViewById(R.id.save_access_points_btn);
        room_picker = (Spinner) findViewById(R.id.room_picker_spinner);
        point_picker = (Spinner) findViewById(R.id.location_picker_spinner);

        FloorListAdapter room_adapter = new FloorListAdapter(this, new ArrayList<Floor>());
        room_picker.setAdapter(room_adapter);

        GetFloorsAsyncTask fill_room_drop_down = new GetFloorsAsyncTask(getString(R.string.get_room_names_url),
                room_adapter,
                null /* params */);
        fill_room_drop_down.execute();

        room_picker.setOnItemSelectedListener(this);

        saveBtn.setOnClickListener(this);
        getAPs.setOnClickListener(this);
        whereAmI.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.building_mapper, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        Location selected_location;
        if (! isWifiEnabled()) {
            Toast.makeText(this, "Turn your wifi on, Dan", Toast.LENGTH_LONG).show();
            return;
        }
        switch (view.getId()) {
            case R.id.get_access_points_btn:
                Toast.makeText(this, "About to do 1 scan", Toast.LENGTH_LONG).show();
                getAccessPoints(1, false /* upload */, 0 /* id, not used because not uploading */);
                break;
            case R.id.save_access_points_btn:
                Toast.makeText(this, "About to do 30 scans", Toast.LENGTH_LONG).show();
                selected_location = (Location) point_picker.getSelectedItem();
                getAccessPoints(30, true /* upload */, selected_location.getId());
                int num_items = point_picker.getAdapter().getCount() - 1;
                int next_pos = point_picker.getSelectedItemPosition() + 4;
                int next_index = num_items < next_pos ? num_items : next_pos;
                //point_picker.setSelection(next_index);
                break;
            case R.id.where_am_i_btn:
                Toast.makeText(this, "About to do 2 scans", Toast.LENGTH_LONG).show();
                getAccessPoints(2, true /* upload */, -1 /* id, not used because not uploading */);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Floor room = (Floor) adapterView.getItemAtPosition(i);
        ArrayAdapter<String> adapter = (ArrayAdapter) point_picker.getAdapter();
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        ArrayAdapter<String> points_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, new ArrayList<String>());
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("building", room.getBuilding()));
        params.add(new BasicNameValuePair("floor", String.valueOf(room.getFloor())));

        ImageView image = (ImageView) findViewById(R.id.floorImage); //Requires view to have floorImage as id

        Fimage = new FloorMapImage(room.getBuilding(), room.getFloor(), image, this);
        point_picker.setOnItemSelectedListener(Fimage);

        String url = getString(R.string.get_access_points_url) + "/" + room.getBuilding() + "/" + String.valueOf(room.getFloor());

        point_picker.setAdapter(points_adapter);
        //GetPointsAsyncTask fill_points_drop_down = new GetPointsAsyncTask(getString(R.string.get_access_points_url), points_adapter, params);
        GetPointsAsyncTask fill_points_drop_down = new GetPointsAsyncTask(url, points_adapter, null);
        fill_points_drop_down.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d("BUILDINGMAPPER", "NOTHING SELECTED");
        Spinner point_picker = (Spinner) findViewById(R.id.location_picker_spinner);
        ArrayAdapter<String> adapter = (ArrayAdapter)point_picker.getAdapter();
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}
