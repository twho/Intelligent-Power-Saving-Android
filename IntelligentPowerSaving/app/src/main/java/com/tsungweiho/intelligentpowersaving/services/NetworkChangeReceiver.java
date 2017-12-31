package com.tsungweiho.intelligentpowersaving.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tsungweiho.intelligentpowersaving.IntelligentPowerSaving;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.utils.NetworkUtils;

/**
 * Created by Tsung Wei Ho on 2017/3/8.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    private NetworkUtils networkUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = (wifi != null && wifi.isConnectedOrConnecting()) || (mobile != null && mobile.isConnectedOrConnecting());

        networkUtils = new NetworkUtils();
        networkUtils.checkNetworkConnection();
        if (!isConnected) {
            if (null != MainActivity.getContext())
                ((MainActivity) MainActivity.getContext()).setConnectionMessage(false);
        }
    }
}
