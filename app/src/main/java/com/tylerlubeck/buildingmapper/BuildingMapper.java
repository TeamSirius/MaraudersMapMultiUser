package com.tylerlubeck.buildingmapper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;


public class BuildingMapper extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_mapper);
        Button getAPs = (Button) findViewById(R.id.get_access_points_btn);
        Spinner point_picker = (Spinner) findViewById(R.id.location_picker_spinner);
        Spinner room_picker = (Spinner) findViewById(R.id.room_picker_spinner);

        ArrayList<String> empty_drop_down = new ArrayList<String>();
        ArrayAdapter<String> points_adapter  = new ArrayAdapter<String>(this,
                                                                 android.R.layout.simple_list_item_1,
                                                                 android.R.id.text1, empty_drop_down);

        ArrayAdapter<String> room_adapter  = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, empty_drop_down);



        point_picker.setAdapter(points_adapter);
        room_picker.setAdapter(room_adapter);

        FillDropDownAsyncTask fill_points_drop_down = new FillDropDownAsyncTask(getString(R.string.get_access_points_url), points_adapter);
        FillDropDownAsyncTask fill_room_drop_down = new FillDropDownAsyncTask(getString(R.string.get_room_names_url), room_adapter);

        fill_points_drop_down.execute();
        fill_room_drop_down.execute();


        getAPs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView items = (ListView) findViewById(R.id.list_of_access_points);
                Context context = getApplicationContext();
                AccessPointManager ap = new AccessPointManager(context);
                AccessPointListAdapter accessPointListAdapter = new AccessPointListAdapter(context, ap.getAccessPoints());
                items.setAdapter(accessPointListAdapter);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.building_mapper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
