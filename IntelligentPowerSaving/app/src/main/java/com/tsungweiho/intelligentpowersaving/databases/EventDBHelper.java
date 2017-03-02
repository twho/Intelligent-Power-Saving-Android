package com.tsungweiho.intelligentpowersaving.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.objects.Event;

import java.util.ArrayList;

/**
 * Created by Tsung Wei Ho on 2015/5/23.
 * Created by Tsung Wei Ho on 2017/2/18.
 */

public class EventDBHelper extends SQLiteOpenHelper implements DBConstants {

    public static final String TABLE_NAME = "event_details";
    public static final String DB_NAME = TABLE_NAME + ".db.sqlite";

    public EventDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                DB_EVENT_UNID + " VARCHAR(30)," +
                DB_EVENT_DETAIL + " VARCHAR(30)," +
                DB_EVENT_POS + " VARCHAR(30)," +
                DB_EVENT_IMG + " TEXT," +
                DB_EVENT_POSTER + " TEXT," +
                DB_EVENT_TIME + " VARCHAR(30)," +
                DB_EVENT_IF_FIXED + " VARCHAR(10)" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Boolean checkIfExist(String uniqueId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_EVENT_UNID};
        String whereClause = DB_EVENT_UNID + " = ?;";
        String[] whereArgs = {uniqueId};

        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        Boolean ifExist;
        if (cursor.getCount() != 0) {
            ifExist = true;
        } else {
            ifExist = false;
        }
        cursor.close();

        return ifExist;
    }

    public long insertDB(Event event) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_EVENT_UNID, event.getUniqueId());
        values.put(DB_EVENT_DETAIL, event.getDetail());
        values.put(DB_EVENT_POS, event.getPosition());
        values.put(DB_EVENT_IMG, event.getImage());
        values.put(DB_EVENT_POSTER, event.getPoster());
        values.put(DB_EVENT_TIME, event.getTime());
        values.put(DB_EVENT_IF_FIXED, event.getIfFixed());

        long rowId = db.insert(TABLE_NAME, null, values);

        return rowId;
    }

    public int updateDB(Event event) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_EVENT_UNID, event.getUniqueId());
        values.put(DB_EVENT_DETAIL, event.getDetail());
        values.put(DB_EVENT_POS, event.getPosition());
        values.put(DB_EVENT_IMG, event.getImage());
        values.put(DB_EVENT_POSTER, event.getPoster());
        values.put(DB_EVENT_TIME, event.getTime());
        values.put(DB_EVENT_IF_FIXED, event.getIfFixed());
        String whereClause = DB_EVENT_UNID + "='" + event.getUniqueId() + "'";

        int count = db.update(TABLE_NAME, values, whereClause, null);

        return count;
    }

    public ArrayList<Event> getAllEventList() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Event> eventList = new ArrayList<Event>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String uniqueId = cursor.getString(1);
            String detail = cursor.getString(2);
            String position = cursor.getString(3);
            String imgUrl = cursor.getString(4);
            String poster = cursor.getString(5);
            String time = cursor.getString(6);
            String ifFixed = cursor.getString(7);
            Event event = new Event(uniqueId, detail, position, imgUrl, poster, time, ifFixed);
            eventList.add(event);
        }
        cursor.close();

        return eventList;
    }

    public Event getEventByUnId(String unId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_EVENT_UNID, DB_EVENT_DETAIL, DB_EVENT_POS, DB_EVENT_IMG, DB_EVENT_POSTER, DB_EVENT_TIME, DB_EVENT_IF_FIXED};
        String whereClause = DB_EVENT_UNID + " = ?;";
        String[] whereArgs = {unId};
        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs,
                null, null, null);
        Event event = null;
        while (cursor.moveToNext()) {
            String uniqueId = cursor.getString(0);
            String detail = cursor.getString(1);
            String position = cursor.getString(2);
            String img = cursor.getString(3);
            String poster = cursor.getString(4);
            String time = cursor.getString(5);
            String ifFixed = cursor.getString(6);
            event = new Event(uniqueId, detail, position, img, poster, time, ifFixed);
        }
        cursor.close();
        db.close();
        return event;
    }

    public void deleteByUniqueId(String uniqueId) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = DB_EVENT_UNID + " = ?;";
        String[] whereArgs = {uniqueId};
        db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public void deleteAllDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public void closeDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }
}
