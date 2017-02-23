package com.tsungweiho.intelligentpowersaving;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by tsung on 2017/2/23.
 */

public class IntelligentPowerSaving extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
