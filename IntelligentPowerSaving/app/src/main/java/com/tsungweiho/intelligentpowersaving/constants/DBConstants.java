package com.tsungweiho.intelligentpowersaving.constants;

/**
 * Created by Tsung Wei Ho on 2/18/2017.
 */

public interface DBConstants {

    // Firebase
    String BUILDING_DB = "buildingList";
    String SYSTEM_ACCOUNT = "tsungwei50521@hotmail.com";
    String SYSTEM_PWD = "801020";
    String FDB_NAME = "name";
    String FDB_CONSUMPTION = "consumption";
    String FDB_DETAIL = "detail";
    String FDB_IMGURL = "img_url";
    String LOCAL_BUILDING_JSON = "intelligent-power-saving-export.json";
    String JSON_ARRAY_NAME = "buildingList";

    // For all databases
    int VERSION = 3;
    String ID = "Id";

    // BuildingDBHelper
    String DB_BUILDING_NAME = "BUILDING_NAME";
    String DB_BUILDING_DETAIL = "BUILDING_DETAIL";
    String DB_BUILDING_CONSUMPTION = "BUILDING_CONSUMPTION";
    String DB_BUILDING_IMG_URL = "BUILDING_IMG_URL";
    String DB_BUILDING_IF_FOLLOW = "DB_BUILDING_IF_FOLLOW";

    // EventDBHelper
    String DB_EVENT_UNID = "EVENT_UNID";
    String DB_EVENT_DETAIL = "EVENT_DETAIL";
    String DB_EVENT_POS = "EVENT_POS";
    String DB_EVENT_POSTER = "DB_EVENT_POSTER";
    String DB_EVENT_IMG = "EVENT_IMG";
    String DB_EVENT_TIME = "EVENT_TIME";
    String DB_EVENT_IF_FIXED = "EVENT_IF_FIXED";

    // MessageDBHelper
    String DB_MESSAGE_UNID = "MESSAGE_UNID";
    String DB_MESSAGE_SENDER = "MESSAGE_SENDER";
    String DB_MESSAGE_TITLE = "MESSAGE_TITLE";
    String DB_MESSAGE_CONTENT = "MESSAGE_CONTENT";
    String DB_MESSAGE_TIME = "MESSAGE_TIME";
    String DB_MESSAGE_INBOX_LABEL = "MESSAGE_INBOX_LABEL";
    String LABEL_MSG_READ = "read";
    String LABEL_MSG_UNREAD = "unread";
    String LABEL_MSG_INBOX = "inbox";
    String LABEL_MSG_STAR = "star";
    String LABEL_MSG_UNSTAR = "unstar";
    String LABEL_MSG_TRASH = "trash";
    String SEPARATOR_MSG_LABEL = ",";
}
