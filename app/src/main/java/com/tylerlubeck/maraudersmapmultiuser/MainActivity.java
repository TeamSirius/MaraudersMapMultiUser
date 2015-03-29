package com.tylerlubeck.maraudersmapmultiuser;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.LoggingBehavior;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Tyler on 2/20/2015.
 */
public class MainActivity extends Activity {

    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /**
     * Create the Activity Instance
     * @param savedInstanceState    Any saved instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.basic_layout);

        if ( ! this.checkPlayServices()) {
            Log.e("MARAUDERSMAP", "No Google Play Services Detected");
            Toast.makeText(this, "No Google Play Services Detected", Toast.LENGTH_SHORT).show();
        }

        if (findViewById(R.id.fragment_container) != null) {
            /* If there is a saved instance, then the fragment is already showing */
            if (savedInstanceState != null) {
                return;
            }

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            /*
            if (session == null) {
                Log.e("MARAUDERSMAP", "Session IS null");
                //If the user is not logged in, then show the login fragment
                FacebookLoginFragment facebookLoginFragment = new FacebookLoginFragment();
                facebookLoginFragment.setArguments(getIntent().getExtras());
                transaction.add(R.id.fragment_container, facebookLoginFragment);
            } else {
                Log.e("MARAUDERSMAP", "Session is NOT null");
                // Otherwise create the mapper fragment and display it
                BuildingMapperFragment buildingMapperFragment = new BuildingMapperFragment();
                buildingMapperFragment.setArguments(getIntent().getExtras());
                transaction.add(R.id.fragment_container, buildingMapperFragment);
            }
            */
            BuildingMapperFragment buildingMapperFragment = new BuildingMapperFragment();
            buildingMapperFragment.setArguments(getIntent().getExtras());
            transaction.add(R.id.fragment_container, buildingMapperFragment);

            /*
            if (session == null) {
                transaction.addToBackStack(null);
                FacebookLoginFragment facebookLoginFragment = new FacebookLoginFragment();
                facebookLoginFragment.setArguments(getIntent().getExtras());
                transaction.add(R.id.fragment_container, facebookLoginFragment);
            }
            */

            transaction.addToBackStack(null).commit();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        this.checkPlayServices();
    }

    /**
     * Create a menu when the button is pressed
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.building_mapper, menu);
        return true;
    }

    /**
     * When a menu item is chosen, process it.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                PrefsFragment prefsFragment = new PrefsFragment();
                prefsFragment.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, prefsFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.facebook_login_setting:
                Toast.makeText(this, "YOU'RE USING THE WRONG ACTIVITY. SHOULD NOT SEE THIS", Toast.LENGTH_LONG).show();
                break;
        }

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private  boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode,
                                                      this,
                                                      PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("MARAUDERSMAP", "This device is not supported");
                finish();
            }

            return false;
        }

        return true;
    }
}
