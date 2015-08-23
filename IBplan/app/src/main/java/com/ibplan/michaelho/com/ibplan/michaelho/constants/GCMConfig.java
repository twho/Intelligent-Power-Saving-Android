package com.ibplan.michaelho.com.ibplan.michaelho.constants;

/**
 * Created by MichaelHo on 2015/5/11.
 */
public interface GCMConfig {

    static final String SERVER_REGISTER ="http://140.118.7.117/ibuildings_html/gcm_register.php";
    public final static String SENDER_ID  = "196960188389";
    public final static String TAG = "IBplan GCM";

    public final static String PREFS_FILE = "prefs";
    public final static String PROPERTY_REG_ID = "regId";
    public final static String PROPERTY_APP_VERSION = "appVersion";
    public final static String URL = "http://10.0.2.2/www/gcm_server_php/register.php";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    // A project number you acquire from the API console


    static final String DISPLAY_MESSAGE_ACTION ="com.ibplan.michaelho.com.ibplan.michaelho.gcm.DISPLAY_MESSAGE";
    static final String EXTRA_MESSAGE = "message";
}
