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
 * Created by tsung on 2017/2/18.
 */

public class BuildingDBHelper extends SQLiteOpenHelper implements DBConstants {
    public static final String DBNAME = "buildingdb.sqlite";
    public static final int VERSION = 1;
    public static final String TABLENAME = "building_details";

    public BuildingDBHelper(Context context) {
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
        String sql = "SELECT Name FROM " + TABLENAME +
                " WHERE name LIKE ?";
        String[] args = {"%" + name + "%"};
        Cursor cursor = db.rawQuery(sql, args);
        ArrayList<String> locations = new ArrayList<String>();
        int columnCount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            String nameLocation = "";
            for (int i = 0; i < columnCount; i++)
                nameLocation += cursor.getString(i) + "\n  ";
            locations.add(nameLocation);
        }
        cursor.close();
        db.close();
        if (locations.size() > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
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
}
