package com.tsungweiho.intelligentpowersaving.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.tsungweiho.intelligentpowersaving.MainActivity;

/**
 * Class for managing permissions needed by the app
 * <p>
 * This class is used to request permissions needed by the app
 *
 * @author Tsung Wei Ho
 * @version 0225.2017
 * @since 1.0.0
 */
public class PermissionManager {
    private Context context;

    // Permissions
    public static final int PERMISSION_ALL = 1;
    private static final int PERMISSION_CAMERA = 2;

    public static String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CAMERA};

    /**
     * PermissionManager constructor
     *
     * @param context the context that calls this class
     */
    public PermissionManager(Context context) {
        this.context = context;
    }

    /**
     * Get if the user already grants all permissions needed by app
     *
     * @param context the context that calls this method
     * @return the boolean indicates if the user already grants all permissions
     */
    public static boolean hasAllPermissions(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Request camera permission from user
     */
    public void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context, android.Manifest.permission.CAMERA)) {
                // TODO add content
            } else {
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[]{android.Manifest.permission.CAMERA},
                        PERMISSION_CAMERA);
            }
        }
    }
}
