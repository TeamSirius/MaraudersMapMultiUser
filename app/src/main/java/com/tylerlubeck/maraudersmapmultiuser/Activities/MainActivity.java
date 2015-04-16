package com.tylerlubeck.maraudersmapmultiuser.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.widget.DrawerLayout;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tylerlubeck.maraudersmapmultiuser.Fragment.BuildingMapperFragment;
import com.tylerlubeck.maraudersmapmultiuser.Fragment.NavigationDrawerFragment;
import com.tylerlubeck.maraudersmapmultiuser.R;
import com.tylerlubeck.maraudersmapmultiuser.Fragment.SelectFriendFragment;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String LOG_TAG = "MARAUDERS_MAP";
    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private DrawerLayout drawerLayout;

    private int currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        /* TODO: Add useful configurations */
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);


        setContentView(R.layout.activity_navigation);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);
        this.currentFragment = -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.onNavigationDrawerItemSelected(0);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (this.currentFragment == position) return; // Don't navigate to something you're already at

        this.currentFragment = position;
        switch (position) {
            case 0:
                Bundle extras = this.getIntent().getExtras();
                BuildingMapperFragment buildingMapperFragment = new BuildingMapperFragment();
                if (extras != null) {
                    buildingMapperFragment.setArguments(extras);
                }
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, buildingMapperFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case 1:
                SelectFriendFragment selectFriendFragment = new SelectFriendFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, selectFriendFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case 2:
                LoginManager.getInstance().logOut();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
