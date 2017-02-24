package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Tsung Wei Ho on 2017/2/23.
 */

public class TimeUtilities {
    private Context context;

    public TimeUtilities(Context context) {
        this.context = context;
    }

    public String getTimeMillies() {
        return Calendar.getInstance().getTimeInMillis() + "";
    }

    public String getDate() {
        DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
        return df1.format(Calendar.getInstance().getTime());
    }

    public String getTimeHHmm() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(Calendar.getInstance().getTime());
    }
}
