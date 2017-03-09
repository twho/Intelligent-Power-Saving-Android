package com.tsungweiho.intelligentpowersaving.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.tsungweiho.intelligentpowersaving.MainActivity;

/**
 * Created by tsung on 2017/2/25.
 */

public class PermissionManager {
    private Context context;

    public static final int PERMISSION_ALL = 1;
    public static String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CAMERA};
    public static final int PERMISSION_CAMERA = 2;

    public PermissionManager(Context context) {
        this.context = context;
    }

    public static boolean hasAllPermissions(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (int i = 0; i < permissions.length; i++) {
                if (ActivityCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context,
                    android.Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[]{android.Manifest.permission.CAMERA},
                        PERMISSION_CAMERA);
            }
        }
    }
}
