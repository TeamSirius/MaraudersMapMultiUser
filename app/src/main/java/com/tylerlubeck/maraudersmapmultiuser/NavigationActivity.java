package com.tylerlubeck.maraudersmapmultiuser;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class NavigationActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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
        FacebookSdk.sdkInitialize(getApplicationContext());
        this.forceLoginIfNecessary();
        super.onCreate(savedInstanceState);

        /* TODO: Add useful configurations */
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();

        ImageLoader.getInstance().init(config);

        setContentView(R.layout.activity_navigation);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.forceLoginIfNecessary();
        this.currentFragment = -1;
        this.onNavigationDrawerItemSelected(0);
    }

    void forceLoginIfNecessary() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (this.currentFragment == position) return; // Don't navigate to something you're already at

        this.currentFragment = position;
        switch (position) {
            case 0:
                BuildingMapperFragment buildingMapperFragment = new BuildingMapperFragment();
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
                this.forceLoginIfNecessary();
                break;
        }


    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.navigation, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
