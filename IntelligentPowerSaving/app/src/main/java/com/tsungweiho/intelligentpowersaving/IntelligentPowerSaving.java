package com.tsungweiho.intelligentpowersaving;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Application provides app-level configuration
 *
 * @author Tsung Wei Ho
 * @version 1227.2017
 * @since 1.0.0
 */
public class IntelligentPowerSaving extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return context;
    }
}
