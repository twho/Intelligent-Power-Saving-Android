package com.ibplan.michaelho.com.ibplan.michaelho.constants;

/**
 * Created by MichaelHo on 2015/4/7.
 */
public interface URLConstants {
    public static final String SERVER_IP = "http://140.118.7.117/";
    public static final String IBUILDINGS_FOLDER = "ibuildings_html/";

    // PHPs
    public static final String URL_GET_BUILDING_DETAIl = SERVER_IP
            + IBUILDINGS_FOLDER + "get_building_detail.php";
    public static final String URL_REGISTER_MEMBER = SERVER_IP
            + IBUILDINGS_FOLDER + "gcm_register.php";
    public static final String URL_CHECK_MAC = SERVER_IP
            + IBUILDINGS_FOLDER + "check_mac.php";
    public static final String URL_POST_EVENTS = SERVER_IP
            + IBUILDINGS_FOLDER + "post_campus_events.php";
    public static final String URL_GET_EVENTS = SERVER_IP
            + IBUILDINGS_FOLDER + "get_campus_events.php";
    public static final String URL_POST_PORTRAIT = SERVER_IP
            + IBUILDINGS_FOLDER + "post_portrait.php";
    public static final String URL_GET_MY_PORTRAIT = SERVER_IP
            + IBUILDINGS_FOLDER + "get_my_portrait.php";
}
