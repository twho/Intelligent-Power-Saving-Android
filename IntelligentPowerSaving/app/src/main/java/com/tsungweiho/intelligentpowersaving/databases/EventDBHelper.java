package com.tsungweiho.intelligentpowersaving.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.objects.Building;

import java.util.ArrayList;

/**
 * Created by Tsung Wei Ho on 2015/5/23.
 * Created by Tsung Wei Ho on 2017/2/18.
 */

public class EventDBHelper extends SQLiteOpenHelper implements DBConstants {

    public static final String DBNAME = "ips.db.sqlite";
    public static final int VERSION = 1;
    public static final String TABLENAME = "event_details";

    public EventDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLENAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                BUILDING_NAME + " VARCHAR(15)," +
                BUILDING_DETAIL + " VARCHAR(30)," +
                BUILDING_CONSUMPTION + " TEXT," +
                BUILDING_IMG_URL + " TEXT" + ");");
    }

    public Boolean checkIfExist(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + BUILDING_NAME + " FROM " + TABLENAME +
                " WHERE " + BUILDING_NAME + " LIKE ?";
        String[] args = {"%" + name + "%"};
        Cursor cursor = db.rawQuery(sql, args);
        Boolean ifExist;
        if (cursor.moveToNext()) {
            ifExist = true;
        } else {
            ifExist = false;
        }
        cursor.close();
        db.close();
        return ifExist;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        onCreate(db);
    }

    public long insertDB(Building building) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BUILDING_NAME, building.getName());
        values.put(BUILDING_DETAIL, building.getDetail());
        values.put(BUILDING_CONSUMPTION, building.getConsumption());
        values.put(BUILDING_IMG_URL, building.getImageUrl());
        long rowId = db.insert(TABLENAME, null, values);
        db.close();
        return rowId;
    }

    public ArrayList<Building> getAllBuildingList() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Building> buildingList = new ArrayList<Building>();
        String sql = "SELECT * FROM " + TABLENAME;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            String detail = cursor.getString(2);
            String consumption = cursor.getString(3);
            String imgUrl = cursor.getString(4);
            Building building = new Building(name, detail, consumption, imgUrl);
            buildingList.add(building);
        }
        cursor.close();
        db.close();
        return buildingList;
    }
}
