package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.tsungweiho.intelligentpowersaving.MainActivity;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;
import com.tsungweiho.intelligentpowersaving.utils.ImageUtils;

/**
 * Created by Tsung Wei Ho on 2017/2/28.
 */

public class SharedPreferencesManager implements DBConstants {
    private Context context;
    private SharedPreferences sharedPreferences;

    // MyAccountInfo
    private String PREF_USER_ACCOUNT = "PREF_EMAIL";

    // Inbox Fragment use
    private String CURRENT_INBOX = "CURRENT_INBOX";

    // Home Fragment use
    private String CURRENT_DISPLAY_MODE = "CURRENT_DISPLAY_MODE";

    private static final SharedPreferencesManager ourInstance = new SharedPreferencesManager();

    public static SharedPreferencesManager getInstance() {
        return ourInstance;
    }

    private SharedPreferencesManager() {
        this.context = MainActivity.getContext();
        this.sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void saveMyAccountInfo(MyAccountInfo myAccountInfo) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String savedAccount = gson.toJson(myAccountInfo);
        prefsEditor.putString(PREF_USER_ACCOUNT, savedAccount);
        prefsEditor.apply();
    }

    public MyAccountInfo getMyAccountInfo() {
        Gson gson = new Gson();
        String defaultAccount = gson.toJson(new MyAccountInfo(context.getString(R.string.testing_account), context.getString(R.string.testing_name), "", "1,1"));
        String json = sharedPreferences.getString(PREF_USER_ACCOUNT, defaultAccount);
        return gson.fromJson(json, MyAccountInfo.class);
    }

    public String getCurrentMessagebox() {
        return sharedPreferences.getString(CURRENT_INBOX, LABEL_MSG_INBOX);
    }

    public void saveCurrentMessageBox(String currentBox) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(CURRENT_INBOX, currentBox);
        prefsEditor.apply();
    }

    public boolean getIfShowFollowedBuilding() {
        return sharedPreferences.getBoolean(CURRENT_DISPLAY_MODE, false);
    }

    public void saveIfShowFollowedBuilding(boolean ifShowFollowedBuilding) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(CURRENT_DISPLAY_MODE, ifShowFollowedBuilding);
        prefsEditor.apply();
    }
}
