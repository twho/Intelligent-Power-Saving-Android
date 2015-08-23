package com.ibplan.michaelho.com.ibplan.michaelho.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MichaelHo on 2015/4/15.
 */

public class TimeUtilities {
    private static final String TAG = "TimeUtilities";

    /**
     * get hour:minute
     *
     * @return ex. 00:00, 12:32
     */
    public static String getTimehhmm() {
        return (String) android.text.format.DateFormat.format("hh:mm", new Date());
    }

    /**
     * get hour:minute
     *
     * @return ex. 00:00, 12:32
     */
    public static String getTimehhmm(Date d) {
        return (String) android.text.format.DateFormat.format("hh:mm", d);
    }

    public static String getTimeyyyy_MM_dd_HH_mm(Date d) {
        return (String) android.text.format.DateFormat.format("yyyy/MM/dd HH:mm", d);
    }

    /**
     * get yyyyMMddhhmmss
     *
     * @return ex. 20141105181533
     */
    public static String getTimeyyyyMMddhhmmss(Date d) {
        return (String) android.text.format.DateFormat.format("yyyyMMddhhmmss", d);
    }

    /**
     * get yyyyMMddhhmmss
     *
     * @return ex. 20141105181533
     */
    public static String getTimeyyyyMMddhhmmss() {
        return (String) android.text.format.DateFormat.format("yyyyMMddhhmmss", new Date());
    }

    public static String getTimeyyyyMMddHHmmss() {
        return (String) android.text.format.DateFormat.format("yyyyMMddHHmmss", new Date());
    }

    public static String getTimeyyyyMMddHHmm() {
        return (String) android.text.format.DateFormat.format("yyyyMMddHHmm", new Date());
    }

    public static String getTimeyyyy_MM_dd_HH_mm() {
        return (String) android.text.format.DateFormat.format("yyyy/MM/dd HH:mm", new Date());
    }

    public static String getTimeyyyyMMdd() {
        return (String) android.text.format.DateFormat.format("yyyyMMdd", new Date());
    }

    //input time string = yyyyMMddHHmm
    public static String StringToTime(String time){
        String readableTime = getTimeyyyy_MM_dd_HH_mm(StringToDate(time));
        return readableTime;
    }

    /**
     * Sample : Date dateYesterday = new Date(new Date().getTime()- (1000 * 60 *
     * 60 * 24)); String yesterday =
     * TimeUtilities.getTimeyyyyMMddhhmmss(dateYesterday);
     * TimeUtilities.isNowOverTime(yesterday);
     *
     * @param deadlineTime
     * @return
     */
    public static boolean isNowOverTime(long deadlineTime) {
        if (StringToLong(getTimeyyyyMMddHHmm()) > deadlineTime) {
            return false;
        }
        return true;
    }

    public static boolean isNowOverTime(String deadlineTime) {
        return isNowOverTime(StringToLong(deadlineTime));
    }

    public static Date StringToDate(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        try {
            Date date = format.parse(time);
            System.out.println(date);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static long StringToLong(String time) {
        return Long.parseLong(time);
    }

    public static String LongToString(long time) {
        return String.valueOf(time);
    }

}
