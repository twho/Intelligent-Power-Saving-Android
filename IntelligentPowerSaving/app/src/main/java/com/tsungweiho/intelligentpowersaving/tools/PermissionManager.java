package com.tsungweiho.intelligentpowersaving.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.tsungweiho.intelligentpowersaving.MainActivity;

/**
 * Created by tsung on 2017/2/25.
 */

public class PermissionManager {
    private Context context;

    public static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    public static final int PERMISSION_ACCESS_FINE_LOCATION = 2;
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 3;
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 4;
    public static final int PERMISSION_ACCESS_NETWORK_STATE = 5;
    public static final int PERMISSION_ACCESS_WIFI_STATE = 6;
    public static final int PERMISSION_CAMERA = 7;

    public PermissionManager(Context context) {
        this.context = context;
    }

    public void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_ACCESS_COARSE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_ACCESS_FINE_LOCATION);
            }
        }
    }

    public void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_READ_EXTERNAL_STORAGE);
            }
        }
    }

    public void requestNetworkPermission() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context,
                    android.Manifest.permission.ACCESS_NETWORK_STATE)) {
            } else {
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                        PERMISSION_ACCESS_NETWORK_STATE);
            }
        }

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context,
                    android.Manifest.permission.ACCESS_WIFI_STATE)) {
            } else {
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[]{android.Manifest.permission.ACCESS_WIFI_STATE},
                        PERMISSION_ACCESS_WIFI_STATE);
            }
        }
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
