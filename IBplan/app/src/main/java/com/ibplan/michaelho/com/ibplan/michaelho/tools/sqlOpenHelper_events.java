package com.ibplan.michaelho.com.ibplan.michaelho.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.EventConstants;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Event;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/5/23.
 */
public class sqlOpenHelper_events extends SQLiteOpenHelper implements EventConstants{

    public static final String DBNAME = "eventsdb.sqlite";
    public static final int VERSION = 1;
    public static final String TABLENAME = "campus_events";


    public sqlOpenHelper_events(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLENAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                STUDENT_NAME + " TEXT, " +
                DEPARTMENT + " TEXT, " +
                LOCATION + " TEXT, " +
                X_POS + " TEXT, " +
                Y_POS + " TEXT, " +
                EVENTS + " TEXT, " +
                IMG + " MEDIUMBLOB, " +
                TIME + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        onCreate(db);
    }

    public Boolean checkIfExist(String location, String eventName, String time){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME +
                " WHERE " + EVENTS + " = ? AND " + LOCATION + " = ? AND " + TIME
                + " = ?", new String[] { eventName, location, time });
        if (cursor != null && cursor.getCount() > 0 ) {
            cursor.close();
            db.close();
            return Boolean.TRUE;
        } else {
            cursor.close();
            db.close();
            return Boolean.FALSE;
        }

    }

    public long insertDB(Event event) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STUDENT_NAME, event.getName());
        values.put(DEPARTMENT, event.getDepartment());
        values.put(LOCATION, event.getLocation());
        values.put(X_POS, event.getPosX());
        values.put(Y_POS, event.getPosY());
        values.put(EVENTS, event.getEvent());
        values.put(IMG, event.getImage());
        values.put(TIME, event.getTime());
        long rowId = db.insert(TABLENAME, null, values);
        db.close();
        return rowId;
    }

    public int UpdateDB(Event event) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STUDENT_NAME, event.getName());
        values.put(DEPARTMENT, event.getDepartment());
        values.put(LOCATION, event.getLocation());
        values.put(X_POS, event.getPosX());
        values.put(Y_POS, event.getPosY());
        values.put(EVENTS, event.getEvent());
        values.put(IMG, event.getImage());
        values.put(TIME, event.getTime());
        String whereClause = STUDENT_NAME + "='" + event.getName() + "'";
        int count = db.update(TABLENAME, values, whereClause, null);
        db.close();
        return count;
    }

    public int deleteDB(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = STUDENT_NAME + "='" + name + "'";
        int count = db.delete(TABLENAME, whereClause, null);
        db.close();
        return count;
    }

    public int deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        int count = db.delete(TABLENAME, null, null);
        db.close();
        return count;
    }

    public Event getSingleEvent(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = { STUDENT_NAME, DEPARTMENT, LOCATION, X_POS, Y_POS, EVENTS, TIME};
        String whereClause = ID + " = ?;";
        String[] whereArgs = { Integer.toString(id) };
        Cursor cursor = db.query(TABLENAME, columns, whereClause, whereArgs,
                null, null, null);
        cursor.moveToFirst();
        cursor.moveToNext();
        String name = cursor.getString(1);
        String department = cursor.getString(2);
        String location = cursor.getString(3);
        String x_pos = cursor.getString(4);
        String y_pos = cursor.getString(5);
        String events = cursor.getString(6);
        byte[] image = cursor.getBlob(7);
        String time = cursor.getString(8);
        Event event = new Event(name, department, location, x_pos, y_pos, events, image, time);
        return event;
    }

    public Event getSingleEvent(String eventName) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = { STUDENT_NAME, DEPARTMENT, LOCATION, X_POS, Y_POS, EVENTS, TIME};
        String whereClause = ID + " = ?;";
        String[] whereArgs = { eventName };
        Cursor cursor = db.query(TABLENAME, columns, whereClause, whereArgs,
                null, null, null);
        cursor.moveToNext();
        String name = cursor.getString(1);
        String department = cursor.getString(2);
        String location = cursor.getString(3);
        String x_pos = cursor.getString(4);
        String y_pos = cursor.getString(5);
        String events = cursor.getString(6);
        byte[] image = cursor.getBlob(7);
        String time = cursor.getString(8);
        Event event = new Event(name, department, location, x_pos, y_pos, events, image, time);
        return event;
    }

    public ArrayList<HashMap<String, Object>> getAllEvents() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<HashMap<String, Object>> events = new ArrayList<HashMap<String, Object>>();
        String sql = "SELECT * FROM " + TABLENAME;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put(STUDENT_NAME, cursor.getString(1));
            item.put(DEPARTMENT, cursor.getString(2));
            item.put(LOCATION, cursor.getString(3));
            item.put(X_POS, cursor.getString(4));
            item.put(Y_POS, cursor.getString(5));
            item.put(EVENTS, cursor.getString(6));
            item.put(IMG, cursor.getBlob(7));
            item.put(TIME, cursor.getString(8));
            events.add(item);
        }

        cursor.close();
        db.close();
        return events;
    }
}
