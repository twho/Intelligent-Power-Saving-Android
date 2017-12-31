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
 * Class for handling event database read and write operations
 *
 * This class is used to read and write event data objects in database
 *
 * @author Tsung Wei Ho
 * @version 0218.2017
 * @since 1.0.0
 */
public class EventDBHelper extends SQLiteOpenHelper implements DBConstants {

    private static final String TABLE_NAME = "event_details";
    private static final String DB_NAME = TABLE_NAME + ".db.sqlite";

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
                DB_EVENT_POSTERIMG + " TEXT," +
                DB_EVENT_TIME + " VARCHAR(30)," +
                DB_EVENT_IS_FIXED + " VARCHAR(10)" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Check if instance already exists in database
     *
     * @param uniqueId the uniqueId of the event data object
     * @return boolean that indicate if the data instance exists in database
     */
    public Boolean isExist(String uniqueId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_EVENT_UNID};
        String whereClause = DB_EVENT_UNID + " = ?;";
        String[] whereArgs = {uniqueId};

        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        Boolean isExist = cursor.getCount() != 0;

        cursor.close();
        db.close();

        return isExist;
    }

    /**
     * Insert new instance to database
     *
     * @param event the event object to be inserted
     * @return the result of inserting event object
     */
    public long insertDB(Event event) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_EVENT_UNID, event.getUniqueId());
        values.put(DB_EVENT_DETAIL, event.getDetail());
        values.put(DB_EVENT_POS, event.getPosition());
        values.put(DB_EVENT_IMG, event.getImage());
        values.put(DB_EVENT_POSTER, event.getPoster());
        values.put(DB_EVENT_POSTERIMG, event.getPosterImg());
        values.put(DB_EVENT_TIME, event.getTime());
        values.put(DB_EVENT_IS_FIXED, event.getIsFixed());

        long rowId = db.insert(TABLE_NAME, null, values);

        return rowId;
    }

    /**
     * Update existing instance in database
     *
     * @param event the event object to be updated
     * @return the row count of updated event objects
     */
    public int updateDB(Event event) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_EVENT_UNID, event.getUniqueId());
        values.put(DB_EVENT_DETAIL, event.getDetail());
        values.put(DB_EVENT_POS, event.getPosition());
        values.put(DB_EVENT_IMG, event.getImage());
        values.put(DB_EVENT_POSTER, event.getPoster());
        values.put(DB_EVENT_POSTERIMG, event.getPosterImg());
        values.put(DB_EVENT_TIME, event.getTime());
        values.put(DB_EVENT_IS_FIXED, event.getIsFixed());
        String whereClause = DB_EVENT_UNID + "='" + event.getUniqueId() + "'";

        int count = db.update(TABLE_NAME, values, whereClause, null);

        return count;
    }

    /**
     * Get all event objects in database once
     *
     * @return arrayList that contains all event objects in database
     */
    public ArrayList<Event> getAllEventList() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_NAME;

        ArrayList<Event> eventList = new ArrayList<Event>();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
            eventList.add(getEventByCursor(cursor));

        cursor.close();

        return eventList;
    }

    /**
     * Get single event object by its uniqueId
     *
     * @param unId the uniqueId of event object
     * @return the event object with specified uniqueId
     */
    public Event getEventByUnId(String unId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {ID, DB_EVENT_UNID, DB_EVENT_DETAIL, DB_EVENT_POS, DB_EVENT_IMG, DB_EVENT_POSTER, DB_EVENT_POSTERIMG, DB_EVENT_TIME, DB_EVENT_IS_FIXED};
        String whereClause = DB_EVENT_UNID + " = ?;";
        String[] whereArgs = {unId};
        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs,
                null, null, null);
        Event event = null;
        while (cursor.moveToNext())
            event = getEventByCursor(cursor);

        cursor.close();
        db.close();

        return event;
    }

    /**
     * Get event from a cursor
     *
     * @param cursor the cursor of query result
     * @return the event object from input cursor
     */
    private Event getEventByCursor(Cursor cursor) {
        String uniqueId = cursor.getString(1);
        String detail = cursor.getString(2);
        String position = cursor.getString(3);
        String imgUrl = cursor.getString(4);
        String poster = cursor.getString(5);
        String posterImg = cursor.getString(6);
        String time = cursor.getString(7);
        String isFixed = cursor.getString(8);

        return new Event(uniqueId, detail, position, imgUrl, poster, posterImg, time, isFixed);
    }

    /**
     * Delete event object by its uniqueId
     *
     * @param uniqueId the uniqueId of an event object to be deleted
     */
    public void deleteByUniqueId(String uniqueId) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = DB_EVENT_UNID + " = ?;";
        String[] whereArgs = {uniqueId};
        db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    /**
     * Delete event database
     */
    public void deleteAllDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    /**
     * Close database connection
     */
    public void closeDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }
}
