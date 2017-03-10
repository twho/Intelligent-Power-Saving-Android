package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.tsungweiho.intelligentpowersaving.MainActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by AKiniyalocts on 1/15/15.
 * <p>
 * Basic network utils
 */
public class NetworkUtilities {
    public static final String TAG = "NetworkUtilities";

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        return false;
    }

    public void checkNetworkConnection() {
        new TaskCheckConnection().execute();
    }

    private class TaskCheckConnection extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlConnection.setRequestProperty("User-Agent", "Test");
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.setConnectTimeout(3000);
                urlConnection.connect();
                return urlConnection.getResponseCode() == 200;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (null != MainActivity.getContext())
                ((MainActivity) MainActivity.getContext()).setIfShowErrorMessage(result);
        }
    }
}
