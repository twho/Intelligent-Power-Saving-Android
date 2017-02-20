package com.tsungweiho.intelligentpowersaving.constants;

/**
 * Created by Tsung Wei Ho on 2017/2/18.
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

    // Imgur
    String IMGUR_CLIENT_ID = "07560f65c2774dc";
    String IMGUR_POST_CLIENT_ID = "Client-ID 07560f65c2774dc";
    String IMGUR_CLIENT_SECRET = "1893bd07d25e1083d272fe4a5069f2e56a03d07c";
    boolean LOGGING = false;

    // BuildingDBHelper
    String ID = "Id";
    String BUILDING_NAME = "BUILDING_NAME";
    String BUILDING_DETAIL = "BUILDING_DETAIL";
    String BUILDING_CONSUMPTION = "BUILDING_CONSUMPTION";
    String BUILDING_IMG_URL = "BUILDING_IMG_URL";
}
