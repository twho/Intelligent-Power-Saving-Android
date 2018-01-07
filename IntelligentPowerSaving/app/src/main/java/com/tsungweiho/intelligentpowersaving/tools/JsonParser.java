package com.tsungweiho.intelligentpowersaving.tools;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.tsungweiho.intelligentpowersaving.IPowerSaving;
import com.tsungweiho.intelligentpowersaving.R;
import com.tsungweiho.intelligentpowersaving.constants.BuildingConstants;
import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.databases.BuildingDBHelper;
import com.tsungweiho.intelligentpowersaving.objects.Building;
import com.tsungweiho.intelligentpowersaving.objects.Event;
import com.tsungweiho.intelligentpowersaving.objects.Message;
import com.tsungweiho.intelligentpowersaving.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Class for converting JSON format data to app data objects
 * <p>
 * This singleton class is used to convert JSON format data to available app data objects
 *
 * @author Tsung Wei Ho
 * @version 1231.2017
 * @since 2.0.0
 */
public class JsonParser implements PubNubAPIConstants, DBConstants, BuildingConstants {
    private String TAG = "JsonParser";

    private static final JsonParser instance = new JsonParser();

    /**
     * Get singleton class instance
     *
     * @return class instance
     */
    public static JsonParser getInstance() {
        return instance;
    }

    private JsonParser() {
    }

    /**
     * Get application context for chart use
     *
     * @return application context
     */
    private Context getContext() {
        return IPowerSaving.getContext();
    }

    /**
     * Load event object from specified JSON object
     *
     * @param jsonObject the JSON object to load event object from
     * @return the event object loaded from JSON object
     */
    public Event getEventByJSONObj(JSONObject jsonObject) {
        Event event = null;

        try {
            String uniqueId = jsonObject.getString(EVENT_UNID);
            String detail = jsonObject.getString(EVENT_DETAIL);
            String position = jsonObject.getString(EVENT_POS);
            String image = jsonObject.getString(EVENT_IMG);
            String poster = jsonObject.getString(EVENT_POSTER);
            String posterImg = jsonObject.getString(EVENT_POSTERIMG);
            String time = jsonObject.getString(EVENT_TIME);
            String isFixed = jsonObject.getString(EVENT_IF_FIXED);
            event = new Event(uniqueId, detail, position, image, poster, posterImg, time, isFixed);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return event;
    }

    /**
     * Get message object from input string
     *
     * @param strMessage the string of message that contains message object parameters
     * @return the message object retrieved from strMessage
     */
    public Message getMessageByString(String strMessage) {
        String uniqueId = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_UNID];
        String title = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_TITLE];
        String content = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_CONTENT];
        String sender = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_SENDER];
        String senderImg = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_SENDER_UID];
        String time = TimeUtils.getInstance().getTimeByMillies(strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_UNID]);
        String inboxLabel = strMessage.split(FROM_WEB_MESSAGE_SEPARATOR)[FROM_WEB_MESSAGE_INBOX_LABEL];

        return new Message(uniqueId, title, content, sender, senderImg, time, inboxLabel);
    }

    /**
     * Convert an event object to a message object
     *
     * @param jsonObject the JSON object represents information of an event object
     * @return the message object retrieved from JSON object
     */
    public Message convertEventToMessage(JSONObject jsonObject) {
        Message message = null;

        try {
            String uniqueId = jsonObject.getString(EVENT_UNID);
            String detail = jsonObject.getString(EVENT_DETAIL);
            String eventImg = jsonObject.getString(EVENT_IMG);
            String address = getAddressByPosition(jsonObject.getString(EVENT_POS));
            String poster = jsonObject.getString(EVENT_POSTER);
            String posterImg = jsonObject.getString(EVENT_POSTERIMG);
            String time = jsonObject.getString(EVENT_TIME);

            message = new Message(uniqueId, detail, getContext().getString(R.string.event_reported_around) + " " + address, poster, posterImg, time, getContext().getString(R.string.label_default) + eventImg);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return message;
    }

    /**
     * Get address by latitude and longitude position
     *
     * @param position the position in the format of latitude,longitude
     * @return the address as String
     */
    private String getAddressByPosition(String position) {
        String address = "---";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.valueOf(position.split(SEPARATOR_MSG_LABEL)[0]),
                    Double.valueOf(position.split(SEPARATOR_MSG_LABEL)[1]), 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return address;
    }

    /**
     * Load local building data to building database
     */
    public void loadLocalBuildingDataToDB() {
        BuildingDBHelper buildingDBHelper = new BuildingDBHelper(getContext());

        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(loadJSONFromAsset());
            JSONArray jArr = jsonObj.getJSONArray(JSON_ARRAY_NAME);

            for (int i = 0; i < jArr.length(); i++) {
                JSONObject currentObj = jArr.getJSONObject(i);
                String name = currentObj.getString(FDB_NAME);

                // if already have in local, don't override the data
                if (!buildingDBHelper.isExist(name)) {
                    String detail = currentObj.getString(FDB_DETAIL);
                    String efficiency = currentObj.getString(FDB_EFFICIENCY);
                    String consumption = currentObj.getString(FDB_CONSUMPTION);
                    String imgUrl = currentObj.getString(FDB_IMGURL);
                    buildingDBHelper.insertDB(new Building(name, detail, efficiency, consumption, imgUrl, BUILDING_NOT_FOLLOW));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load JSON file from assets
     *
     * @return the JSON file as String
     */
    private String loadJSONFromAsset() {
        String json = null;
        InputStream is;
        try {
            is = getContext().getAssets().open(LOCAL_BUILDING_JSON);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return json;
    }
}
