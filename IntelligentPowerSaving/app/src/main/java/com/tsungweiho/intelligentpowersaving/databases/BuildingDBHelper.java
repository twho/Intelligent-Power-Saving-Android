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

    public static final String TABLE_NAME = "building_details";
    public static final String DB_NAME = TABLE_NAME + ".db.sqlite";

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
                DB_BUILDING_CONSUMPTION + " TEXT," +
                DB_BUILDING_IMG_URL + " TEXT" + ");");
    }

    public Boolean checkIfExist(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_BUILDING_NAME};
        String whereClause = DB_BUILDING_NAME + " = ?;";
        String[] whereArgs = {name};

        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        Boolean ifExist;
        if (cursor.getCount() != 0) {
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

        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowId;
    }

    public ArrayList<Building> getAllBuildingList() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Building> buildingList = new ArrayList<Building>();
        String sql = "SELECT * FROM " + TABLE_NAME;
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

    public Building getBuildingByName(String buildingName) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_BUILDING_NAME, DB_BUILDING_DETAIL, DB_BUILDING_CONSUMPTION, DB_BUILDING_IMG_URL};
        String whereClause = DB_BUILDING_NAME + " = ?;";
        String[] whereArgs = {buildingName};
        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs,
                null, null, null);
        Building building = null;
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String detail = cursor.getString(1);
            String consumption = cursor.getString(2);
            String imgUrl = cursor.getString(3);
            building = new Building(name, detail, consumption, imgUrl);
        }
        cursor.close();
        db.close();
        return building;
    }
}
