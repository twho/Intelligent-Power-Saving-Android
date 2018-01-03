package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;

import java.util.UUID;

/**
 * Class for managing share preference in the app
 *
 * This singleton class is used to manage share preferences and local stored information in the app
 *
 * @author Tsung Wei Ho
 * @version 0228.2017
 * @since 1.0.0
 */
public class SharedPrefsUtils implements DBConstants {
    private SharedPreferences sharedPreferences;

    // MyAccountInfo
    private String PREF_USER_ACCOUNT = "PREF_EMAIL";

    // Inbox Fragment use
    private String CURRENT_INBOX = "CURRENT_INBOX";

    // Home Fragment use
    private String CURRENT_DISPLAY_MODE = "CURRENT_DISPLAY_MODE";

    private static final SharedPrefsUtils instance = new SharedPrefsUtils();

    public static SharedPrefsUtils getInstance() {
        return instance;
    }

    private SharedPrefsUtils() {
        this.sharedPreferences = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * Get application context for animation use
     *
     * @return application context
     */
    private Context getContext() {
        return IPowerSaving.getContext();
    }

    /**
     * Init user preference
     */
    public void initMyAccountInfo(){
        if (null == getMyAccountInfo()) {
            synchronized (this) {
                String uid = null == UUID.randomUUID() ? TimeUtils.getInstance().getTimeMillies() : UUID.randomUUID().toString();
                saveMyAccountInfo(new MyAccountInfo(uid, getContext().getString(R.string.testing_account), getContext().getString(R.string.testing_name), "", "1,1"));
            }
        }
    }

    public void saveMyAccountInfo(MyAccountInfo myAccountInfo) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String savedAccount = gson.toJson(myAccountInfo);

        synchronized (this) {
            prefsEditor.putString(PREF_USER_ACCOUNT, savedAccount);
            prefsEditor.apply();
        }
    }

    public MyAccountInfo getMyAccountInfo() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PREF_USER_ACCOUNT, null);

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
