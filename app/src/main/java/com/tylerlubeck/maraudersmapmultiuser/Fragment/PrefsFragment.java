package com.tylerlubeck.maraudersmapmultiuser.Fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.tylerlubeck.maraudersmapmultiuser.R;

/**
 * Created by Tyler on 2/20/2015.
 */
public class PrefsFragment extends PreferenceFragment {

    /**
     * When created, get the preferences from the xml file
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
