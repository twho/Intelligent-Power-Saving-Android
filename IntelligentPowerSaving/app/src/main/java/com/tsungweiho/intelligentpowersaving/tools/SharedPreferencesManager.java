package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.objects.MyAccountInfo;

/**
 * Created by Tsung Wei Ho on 2017/2/28.
 */

public class SharedPreferencesManager implements DBConstants{
    private Context context;
    private SharedPreferences sharedPreferences;

    // MyAccountInfo
    private String PREF_USER_ACCOUNT = "PREF_EMAIL";

    // Inbox Fragment use
    private String CURRENT_INBOX = "CURRENT_INBOX";

    public SharedPreferencesManager(Context context) {
        this.context = context;
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
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String defaultAccount = gson.toJson(new MyAccountInfo(context.getString(R.string.testing_account), context.getString(R.string.testing_name), "", "1,1"));
        String json = sharedPreferences.getString(PREF_USER_ACCOUNT, defaultAccount);
        return gson.fromJson(json, MyAccountInfo.class);
    }

    public String getCurrentMessagebox() {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(CURRENT_INBOX, LABEL_MSG_INBOX);
    }

    public void saveCurrentMessageBox(String currentBox){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(CURRENT_INBOX, currentBox);
        prefsEditor.apply();
    }
}
