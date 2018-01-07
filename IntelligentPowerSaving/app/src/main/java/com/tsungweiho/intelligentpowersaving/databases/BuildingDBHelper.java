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
 * Class for handling building database read and write operations
 * <p>
 * This class is used to read and write building data objects in database
 *
 * @author Tsung Wei Ho
 * @version 0218.2017
 * @since 1.0.0
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

    /**
     * Initialize database
     *
     * @param db currently available database
     */
    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                DB_BUILDING_NAME + " VARCHAR(30)," +
                DB_BUILDING_DETAIL + " TEXT," +
                DB_BUILDING_EFFICIENCY + " VARCHAR(10)," +
                DB_BUILDING_CONSUMPTION + " INTEGER, " +
                DB_BUILDING_IMG_URL + " TEXT," +
                DB_BUILDING_IS_FOLLOW + " VARCHAR(10)" + ");");
    }

    /**
     * Check if the instance already exists in database
     *
     * @param name the name of the building instance
     * @return boolean that indicates if the instance exists
     */
    public Boolean isExist(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_BUILDING_NAME};
        String whereClause = DB_BUILDING_NAME + " = ?;";
        String[] whereArgs = {name};

        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        Boolean isExist = cursor.getCount() != 0;

        cursor.close();
        db.close();

        return isExist;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert new instance to database
     *
     * @param building the building object to be inserted
     * @return the result of inserting building object
     */
    public long insertDB(Building building) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_BUILDING_NAME, building.getName());
        values.put(DB_BUILDING_DETAIL, building.getDetail());
        values.put(DB_BUILDING_EFFICIENCY, building.getEfficiency());
        values.put(DB_BUILDING_CONSUMPTION, building.getConsumption());
        values.put(DB_BUILDING_IMG_URL, building.getImageUrl());
        values.put(DB_BUILDING_IS_FOLLOW, building.getIfFollow());

        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowId;
    }

    /**
     * Update existing instance in database
     *
     * @param building the building object to be updated
     * @return the row count of updated building objects
     */
    public int updateDB(Building building) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_BUILDING_NAME, building.getName());
        values.put(DB_BUILDING_DETAIL, building.getDetail());
        values.put(DB_BUILDING_EFFICIENCY, building.getEfficiency());
        values.put(DB_BUILDING_CONSUMPTION, building.getConsumption());
        values.put(DB_BUILDING_IMG_URL, building.getImageUrl());
        values.put(DB_BUILDING_IS_FOLLOW, building.getIfFollow());
        String whereClause = DB_BUILDING_NAME + "='" + building.getName() + "'";

        return db.update(TABLE_NAME, values, whereClause, null);
    }

    /**
     * Get all building objects in database
     *
     * @return arrayList that contains all building objects
     */
    public ArrayList<Building> getAllBuildingSet() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, DB_BUILDING_EFFICIENCY + " DESC");
        ArrayList<Building> buildingList = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext())
            buildingList.add(getBuildingByCursor(cursor));

        cursor.close();
        db.close();

        return buildingList;
    }

    /**
     * Get the building that followed by user
     *
     * @return arrayList that contains building objects followed by user
     */
    public ArrayList<Building> getFollowedBuildingSet() {
        SQLiteDatabase db = getReadableDatabase();

        // SQL query
        String[] columns = {ID, DB_BUILDING_NAME, DB_BUILDING_DETAIL, DB_BUILDING_EFFICIENCY, DB_BUILDING_CONSUMPTION, DB_BUILDING_IMG_URL, DB_BUILDING_IS_FOLLOW};
        String whereClause = DB_BUILDING_IS_FOLLOW + " = ?;";
        String[] whereArgs = {"true"};

        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, DB_BUILDING_EFFICIENCY + " DESC");
        ArrayList<Building> buildingList = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()) {
            buildingList.add(getBuildingByCursor(cursor));
        }
        cursor.close();
        db.close();

        return buildingList;
    }

    /**
     * Get building object by its name
     *
     * @param buildingName the name of the building
     * @return the building object with specified building name
     */
    public Building getBuildingByName(String buildingName) {
        SQLiteDatabase db = getReadableDatabase();

        // SQL query
        String[] columns = {ID, DB_BUILDING_NAME, DB_BUILDING_DETAIL, DB_BUILDING_EFFICIENCY, DB_BUILDING_CONSUMPTION, DB_BUILDING_IMG_URL, DB_BUILDING_IS_FOLLOW};
        String whereClause = DB_BUILDING_NAME + " = ?;";
        String[] whereArgs = {buildingName};

        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, null);

        Building building = null;
        while (cursor.moveToNext()) {
            building = getBuildingByCursor(cursor);
        }

        cursor.close();
        db.close();

        return building;
    }

    /**
     * Get building from a cursor
     *
     * @param cursor the cursor of query result
     * @return the building object from input cursor
     */
    private Building getBuildingByCursor(Cursor cursor) {
        String name = cursor.getString(1);
        String detail = cursor.getString(2);
        String efficiency = cursor.getString(3);
        String consumption = cursor.getString(4);
        String imgUrl = cursor.getString(5);
        String ifFollow = cursor.getString(6);

        return new Building(name, detail, efficiency, consumption, imgUrl, ifFollow);
    }
}
