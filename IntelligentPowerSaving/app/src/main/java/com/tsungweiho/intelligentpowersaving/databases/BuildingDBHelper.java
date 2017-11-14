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
 * Created by Tsung Wei Ho on 2015/4/18.
 */

public class BuildingDBHelper extends SQLiteOpenHelper implements DBConstants {

    private static final String TABLE_NAME = "building_details";
    private static final String DB_NAME = TABLE_NAME + ".db.sqlite";

    public BuildingDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                DB_BUILDING_NAME + " VARCHAR(15)," +
                DB_BUILDING_DETAIL + " VARCHAR(30)," +
                DB_BUILDING_CONSUMPTION + " INTEGER," +
                DB_BUILDING_IMG_URL + " TEXT," +
                DB_BUILDING_IF_FOLLOW + " VARCHAR(10)" + ");");
    }

    public Boolean checkIfExist(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_BUILDING_NAME};
        String whereClause = DB_BUILDING_NAME + " = ?;";
        String[] whereArgs = {name};

        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        Boolean ifExist = cursor.getCount() != 0;

        cursor.close();
        db.close();

        return ifExist;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertDB(Building building) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_BUILDING_NAME, building.getName());
        values.put(DB_BUILDING_DETAIL, building.getDetail());
        values.put(DB_BUILDING_CONSUMPTION, building.getConsumption());
        values.put(DB_BUILDING_IMG_URL, building.getImageUrl());
        values.put(DB_BUILDING_IF_FOLLOW, building.getIfFollow());

        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowId;
    }

    public int updateDB(Building building) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_BUILDING_NAME, building.getName());
        values.put(DB_BUILDING_DETAIL, building.getDetail());
        values.put(DB_BUILDING_CONSUMPTION, building.getConsumption());
        values.put(DB_BUILDING_IMG_URL, building.getImageUrl());
        values.put(DB_BUILDING_IF_FOLLOW, building.getIfFollow());
        String whereClause = DB_BUILDING_NAME + "='" + building.getName() + "'";

        return db.update(TABLE_NAME, values, whereClause, null);
    }

    public ArrayList<Building> getAllBuildingSet() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, DB_BUILDING_CONSUMPTION + " DESC");
        ArrayList<Building> buildingList = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()) {
            buildingList.add(getBuildingByCursor(cursor));
        }
        cursor.close();
        db.close();

        return buildingList;
    }

    public ArrayList<Building> getFollowedBuildingSet() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, DB_BUILDING_CONSUMPTION + " DESC");
        ArrayList<Building> buildingList = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()) {
            if (Boolean.parseBoolean(cursor.getString(5))) {
                buildingList.add(getBuildingByCursor(cursor));
            }
        }
        cursor.close();
        db.close();

        return buildingList;
    }

    public Building getBuildingByName(String buildingName) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {ID, DB_BUILDING_NAME, DB_BUILDING_DETAIL, DB_BUILDING_CONSUMPTION, DB_BUILDING_IMG_URL, DB_BUILDING_IF_FOLLOW};
        String whereClause = DB_BUILDING_NAME + " = ?;";
        String[] whereArgs = {buildingName};
        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs,
                null, null, null);
        Building building = null;
        while (cursor.moveToNext()) {
            building = getBuildingByCursor(cursor);
        }
        cursor.close();
        db.close();
        return building;
    }

    private Building getBuildingByCursor(Cursor cursor) {
        String name = cursor.getString(1);
        String detail = cursor.getString(2);
        String consumption = cursor.getString(3);
        String imgUrl = cursor.getString(4);
        String ifFollow = cursor.getString(5);
        return new Building(name, detail, consumption, imgUrl, ifFollow);
    }
}
