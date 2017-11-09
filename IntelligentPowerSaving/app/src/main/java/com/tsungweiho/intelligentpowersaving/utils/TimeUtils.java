package com.tsungweiho.intelligentpowersaving.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Tsung Wei Ho on 2017/2/23.
 */

public class TimeUtils {

    private static final TimeUtils ourInstance = new TimeUtils();

    public static TimeUtils getInstance() {
        return ourInstance;
    }

    private TimeUtils() {}

    public String getTimeMillies() {
        return Calendar.getInstance().getTimeInMillis() + "";
    }

    public String getDate() {
        DateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
        return df1.format(Calendar.getInstance().getTime());
    }

    public String getTimeHH() {
        DateFormat df = new SimpleDateFormat("HH");
        return df.format(Calendar.getInstance().getTime());
    }

    public String getTimehhmm() {
        DateFormat df = new SimpleDateFormat("hh:mm aaa");
        return df.format(Calendar.getInstance().getTime());
    }

    public String getTimehhmmByMillies(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("hh:mm aaa");
        calendar.setTimeInMillis(milliSeconds);
        return df.format(calendar.getTime());
    }

    public String getDateByMillies(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        calendar.setTimeInMillis(milliSeconds);
        return df.format(calendar.getTime());
    }

    public String getTimeByMillies(String milliSeconds) {
        long millies = Long.valueOf(milliSeconds);
        return getDateByMillies(millies) + "," + getTimehhmmByMillies(millies);
    }
}
