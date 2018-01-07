package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;

import java.util.UUID;

/**
 * Class for managing share preference in the app
 * <p>
 * This singleton class is used to manage share preferences and local stored information in the app
 *
 * @author Tsung Wei Ho
 * @version 0228.2017
 * @since 1.0.0
 */
public class SharedPrefsUtils implements DBConstants {
    private SharedPreferences sharedPreferences;

    // Comma separator
    public String SEPARATOR = ",";

    // MyAccountInfo
    private String PREF_USER_ACCOUNT = "PREF_EMAIL";

    public String PREF_REPORT_DRAFT = "PREF_REPORT_DRAFT";

    // Inbox Fragment use
    public String CURRENT_INBOX = "CURRENT_INBOX";

    // Home Fragment use
    private String CURRENT_DISPLAY_MODE = "CURRENT_DISPLAY_MODE";

    private static final SharedPrefsUtils instance = new SharedPrefsUtils();

    /**
     * Get singleton instance
     *
     * @return singleton instance of SharePrefsUtils class
     */
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
    public void initMyAccountInfo() {
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
        String json = sharedPreferences.getString(PREF_USER_ACCOUNT, null);

        return new Gson().fromJson(json, MyAccountInfo.class);
    }

    public String getPreferenceString(String key, String defaultStr) {
        return sharedPreferences.getString(key, defaultStr);
    }

    public void savePreferenceString(String key, String strToSave) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, strToSave);
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
