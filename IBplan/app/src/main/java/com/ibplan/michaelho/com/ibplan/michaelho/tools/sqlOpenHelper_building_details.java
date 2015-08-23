package com.ibplan.michaelho.com.ibplan.michaelho.tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.BuildingConstants;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Location;

/**
 * Created by MichaelHo on 2015/4/14.
 */
public class sqlOpenHelper_building_details extends SQLiteOpenHelper implements BuildingConstants {

    public static final String DBNAME = "tasksdb.sqlite";
    public static final int VERSION = 1;
    public static final String TABLENAME = "building_details";
    public static final String ID = "Id";
    public static final String NAME = "Name";
    public static final String IMG = "Image";
    public static final String DETAIL = "Detail";

    public sqlOpenHelper_building_details(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLENAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                NAME + " VARCHAR(15)," +
                IMG + " MEDIUMBLOB, " +
                DETAIL + " VARCHAR(100)" + ");");
    }

    public Boolean checkIfExist(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLENAME +
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

    public long insertDB(Location location) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, location.getName());
        values.put(DETAIL, location.getDetail());
        values.put(IMG, location.getImage());
        long rowId = db.insert(TABLENAME, null, values);
        db.close();
        return rowId;
    }

    public void insertLocation(Location location) {
        SQLiteDatabase db = getWritableDatabase();
        String delSql = "DELETE FROM " + TABLENAME;
        SQLiteStatement delStmt = db.compileStatement(delSql);
        delStmt.execute();
        String sql = "INSERT INTO " + TABLENAME + " (" + NAME + ", " + DETAIL + ", " + IMG + ") VALUES( ?, ?, ?)";
        SQLiteStatement insertStmt = db.compileStatement(sql);
        insertStmt.clearBindings();
        insertStmt.bindString(1, location.getName());
        insertStmt.bindString(2, location.getDetail());
        insertStmt.bindBlob(3, location.getImage());
        insertStmt.executeInsert();
        db.close();
    }

    public int UpdateDB(Location location) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, location.getName());
        values.put(DETAIL, location.getDetail());
        values.put(IMG, location.getImage());
        String whereClause = NAME + "='" + location.getName() + "'";
        int count = db.update(TABLENAME, values, whereClause, null);
        db.close();
        return count;
    }

    public int deleteDB(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = NAME + "='" + name + "'";
        int count = db.delete(TABLENAME, whereClause, null);
        db.close();
        return count;
    }

    public ArrayList<String> queryDB(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLENAME +
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
        return locations;
    }

    public Location getFullDetail(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = { NAME, DETAIL, IMG};
        String whereClause = ID + " = ?;";
        String[] whereArgs = { Integer.toString(id) };
        Cursor cursor = db.query(TABLENAME, columns, whereClause, whereArgs,
                null, null, null);
        cursor.moveToNext();
        String name = cursor.getString(0);
        String detail = cursor.getString(1);
        byte[] image = cursor.getBlob(2);
        Location location = new Location(name, detail, image);
        return location;
    }

    public ArrayList<HashMap<String, Object>>  getAllBuildingDetail() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<HashMap<String, Object>> locations = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> item = new HashMap<String, Object>();
        String sql = "SELECT * FROM " + TABLENAME;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            item.put(BUILDING_NAME, cursor.getString(0));
            item.put(BUILDING_DETAIL, cursor.getString(1));
            item.put(BUILDING_IMG, cursor.getBlob(2));
            locations.add(item);
        }

        cursor.close();
        db.close();
        return locations;
    }

    public String getImages(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT Image FROM " + TABLENAME +
                " WHERE Name = ?";
        String[] args = {name};
        Cursor cursor = db.rawQuery(sql, args);
        String locations = "";
        int columnCount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            String nameLocation = "";
            for (int i = 0; i < columnCount; i++)
                nameLocation += cursor.getBlob(i);
            locations += nameLocation;
        }
        Log.d("locations", locations);
        cursor.close();
        db.close();

        return locations;
    }

    public String getName(int Id) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT Name FROM " + TABLENAME +
                " WHERE Id = " + Id;
        Cursor cursor = db.rawQuery(sql, null);
        String locations = "";
        int columnCount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            String nameLocation = "";
            for (int i = 0; i < columnCount; i++)
                nameLocation += cursor.getString(i) + "\n  ";
            locations += nameLocation;
        }
        cursor.close();
        db.close();
        return locations;
    }
}
