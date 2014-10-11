package com.tylerlubeck.buildingmapper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


public class BuildingMapper extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_mapper);
        Button getAPs = (Button) findViewById(R.id.get_access_points_btn);

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
