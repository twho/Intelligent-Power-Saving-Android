package com.tsungweiho.intelligentpowersaving.constants;

/**
 * Created by Tsung Wei Ho on 4/15/2015.
 */

public interface BuildingConstants {
    String[] BUILDING_LIST = new String[]{"E1 Building", "E2 Building", "EE Building", "TR Building", "BT Building", "IB Building", "T4 Building"};
    String[] BUILDING_FLOOR = new String[]{"Basement", "1F", "2F", "3F", "4F", "5F", "6F", "7F", "8F", "9F", "10F", "11F"};
    String[] TIME_HOURS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "Noon", "1", "2", "3",
            "4", "5", "6", "7", "8", "9", "10", "11", "0"};
    String SEPARATOR_CONSUMPTION = ",";

    // If users follow the building
    String BUILDING_FOLLOW = "true";
    String BUILDING_NOT_FOLLOW = "false";

    String BUILDING_UNIT = " kWh \n";
}
