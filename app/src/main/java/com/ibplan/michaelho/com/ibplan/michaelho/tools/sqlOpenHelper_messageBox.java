package com.ibplan.michaelho.com.ibplan.michaelho.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.MessageConstants;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.Location;
import com.ibplan.michaelho.com.ibplan.michaelho.objects.SingleMessage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MichaelHo on 2015/6/1.
 */
public class sqlOpenHelper_messageBox extends SQLiteOpenHelper implements MessageConstants{
    public static final String DBNAME = "messageboxdb.sqlite";
    public static final int VERSION = 1;
    public static final String TABLENAME = "table_inbox";

    public static final String ID = "Id";
    public static final String TITLE = "TITLE";
    public static final String DETAIL = "DETAIL";
    public static final String SENDER = "SENDER";
    public static final String TIME = "TIME";
    public static final String IFREAD = "IFREAD";

    public sqlOpenHelper_messageBox(Context context) {
        super(context, DBNAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLENAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                TITLE + " VARCHAR(60)," +
                DETAIL + " TEXT," +
                SENDER + " VARCHAR(60), " +
                TIME + " VARCHAR(60), " +
                IFREAD + " VARCHAR(15)" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        onCreate(db);
    }

    public Boolean checkIfExist(String detail) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLENAME +
                " WHERE name LIKE ?";
        String[] args = {"%" + detail + "%"};
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

    public long insertDB(SingleMessage singleMessage) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, singleMessage.getTitle());
        values.put(DETAIL, singleMessage.getDetail());
        values.put(SENDER, singleMessage.getSender());
        values.put(TIME, singleMessage.getTime());
        values.put(IFREAD, singleMessage.getIfRead());
        long rowId = db.insert(TABLENAME, null, values);
        db.close();
        return rowId;
    }

    public int UpdateDB(SingleMessage singleMessage) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, singleMessage.getTitle());
        values.put(DETAIL, singleMessage.getDetail());
        values.put(SENDER, singleMessage.getSender());
        values.put(TIME, singleMessage.getTime());
        values.put(IFREAD, singleMessage.getIfRead());
        String whereClause = DETAIL + "='" + singleMessage.getDetail() + "'";
        int count = db.update(TABLENAME, values, whereClause, null);
        db.close();
        return count;
    }

    public int UpdateIfRead(String detail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IFREAD, "1");
        String whereClause = DETAIL + "='" + detail + "'";
        int count = db.update(TABLENAME, values, whereClause, null);
        db.close();
        return count;
    }

    public int deleteDB(String detail) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = DETAIL + "='" + detail + "'";
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

    public SingleMessage getFullDetail(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = { TITLE, DETAIL, SENDER, TIME, IFREAD};
        String whereClause = ID + " = ?;";
        String[] whereArgs = { Integer.toString(id) };
        Cursor cursor = db.query(TABLENAME, columns, whereClause, whereArgs,
                null, null, null);
        cursor.moveToNext();
        String title = cursor.getString(0);
        String detail = cursor.getString(1);
        String sender = cursor.getString(2);
        String time = cursor.getString(3);
        String ifread = cursor.getString(4);
        SingleMessage singleMessage = new SingleMessage(title, detail, sender, time, ifread);
        db.close();
        return singleMessage;
    }

    public ArrayList<HashMap<String, Object>>  getAllMessageDetail() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<HashMap<String, Object>> locations = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> item = new HashMap<String, Object>();
        String sql = "SELECT * FROM " + TABLENAME;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            item.put(MESSAGE_TITLE, cursor.getString(0));
            item.put(MESSAGE_DETAIL, cursor.getString(1));
            item.put(MESSAGE_SENDER, cursor.getString(2));
            item.put(MESSAGE_TIME, cursor.getString(3));
            item.put(MESSAGE_IFREAD, cursor.getString(4));
            locations.add(item);
        }

        cursor.close();
        db.close();
        return locations;
    }
}
