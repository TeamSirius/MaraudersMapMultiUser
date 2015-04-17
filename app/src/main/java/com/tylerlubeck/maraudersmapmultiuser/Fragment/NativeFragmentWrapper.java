package com.tylerlubeck.maraudersmapmultiuser.Fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;

/**
 * Created by Tyler on 2/23/2015.
 */

/**
 * Because fuck facebook login:
 * http://stackoverflow.com/questions/24644599/loginbutton-with-native-fragment
 */
@SuppressLint("ValidFragment")
public class NativeFragmentWrapper extends android.support.v4.app.Fragment {
    private final Fragment nativeFragment;

    public NativeFragmentWrapper(Fragment nativeFragment) {
        this.nativeFragment = nativeFragment;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        nativeFragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        nativeFragment.onActivityResult(requestCode, resultCode, data);
    }
}
