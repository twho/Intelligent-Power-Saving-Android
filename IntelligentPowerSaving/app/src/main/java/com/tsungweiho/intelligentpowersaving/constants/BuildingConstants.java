package com.tsungweiho.intelligentpowersaving.constants;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Interface that stores all constants used by BuildingFragment
 *
 * This interface is used to store and handle all constants used by BuildingFragment
 *
 * @author Tsung Wei Ho
 * @version 1221.2017
 * @since 1.0.0
 */
public interface BuildingConstants {
    LatLngBounds ntustBounds = new LatLngBounds(new LatLng(25.011353, 121.540963), new LatLng(25.015593, 121.542648));

    // TODO Will be used when UM version is developed
    LatLngBounds umCentralBounds = new LatLngBounds(new LatLng(42.269298, -83.745612), new LatLng(42.286554, -83.725708));
    LatLngBounds umNorthBounds = new LatLngBounds(new LatLng(42.269298, -83.745612), new LatLng(42.286554, -83.725708));

    String[] TIME_HOURS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "Noon", "1", "2", "3",
            "4", "5", "6", "7", "8", "9", "10", "11", "0"};
    String SEPARATOR_CONSUMPTION = ",";

    // If users follow the building
    String BUILDING_FOLLOW = "true";
    String BUILDING_NOT_FOLLOW = "false";

    String BUILDING_UNIT = " kWh \n";
}
