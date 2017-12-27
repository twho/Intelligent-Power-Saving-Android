package com.tsungweiho.intelligentpowersaving.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Class to perform all time formatting tasks in the app
 *
 * This singleton class consist of all time formatting functions used in the app
 *
 * @author Tsung Wei Ho
 * @version 0223.2017
 * @since 1.0.0
 */
public class TimeUtils {

    private static final TimeUtils ourInstance = new TimeUtils();

    public static TimeUtils getInstance() {
        return ourInstance;
    }

    private TimeUtils() {}

    /**
     * Get time in milliseconds since 1970
     *
     * @return millie seconds as String
     */
    public String getTimeMillies() {
        return Calendar.getInstance().getTimeInMillis() + "";
    }

    /**
     * Get date in format of MM/dd/yyyy
     *
     * @return date as String
     */
    public String getDate() {
        DateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
        return df1.format(Calendar.getInstance().getTime());
    }

    /**
     * Get current hour using 24-hour format
     *
     * @return current hour as String
     */
    public String getTimeHH() {
        DateFormat df = new SimpleDateFormat("HH");
        return df.format(Calendar.getInstance().getTime());
    }

    /**
     * Get current time in format of hh:mm aaa
     *
     * @return current time as String
     */
    public String getTimehhmm() {
        DateFormat df = new SimpleDateFormat("hh:mm aaa");
        return df.format(Calendar.getInstance().getTime());
    }

    /**
     * Get time from milliseconds String
     *
     * @param milliSeconds string milliseconds to be converted to specified time format String
     * @return the time as String in format of MM/dd/yyyy hh:mm aaa
     */
    public String getTimeByMillies(String milliSeconds) {
        long millies = Long.valueOf(milliSeconds);
        return getDateByMilliSec(millies) + "," + getTimehhmmByMilliSec(millies);
    }

    /**
     * Get time in format of hh:mm aaa from milliseconds
     *
     * @param milliSeconds long milliseconds to be converted to specified format String
     * @return time in format of hh:mm aaa as String
     */
    private String getTimehhmmByMilliSec(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("hh:mm aaa");
        calendar.setTimeInMillis(milliSeconds);
        return df.format(calendar.getTime());
    }

    /**
     * Get date in format of MM/dd/yyyy from milliseconds
     *
     * @param milliSeconds long milliseconds to be converted to specified format String
     * @return date in format of MM/dd/yyyy as String
     */
    private String getDateByMilliSec(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        calendar.setTimeInMillis(milliSeconds);
        return df.format(calendar.getTime());
    }
}
