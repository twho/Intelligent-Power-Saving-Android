package com.tsungweiho.intelligentpowersaving;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.pubnub.api.PubNub;
import com.tsungweiho.intelligentpowersaving.tools.PubNubHelper;

/**
 * Application provides app-level configuration
 *
 * @author Tsung Wei Ho
 * @version 1227.2017
 * @since 1.0.0
 */
public class IPowerSaving extends Application {
    private static Context context;

    public static PubNub pubnub = null;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        pubnub = PubNubHelper.getInstance().initPubNub();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * Get context used in app-wide
     *
     * @return the context used in the app
     */
    public static Context getContext() {
        return context;
    }

    /**
     * Get PubNub used in app-wide
     *
     * @return the PubNub object used in the app
     */
    public static PubNub getPubNub() {
        return pubnub;
    }

}
