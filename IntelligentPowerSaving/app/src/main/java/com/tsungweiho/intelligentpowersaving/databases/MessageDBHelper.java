package com.tsungweiho.intelligentpowersaving.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.objects.Message;

import java.util.ArrayList;

/**
 * Created by tsung on 2017/2/24.
 */

public class MessageDBHelper extends SQLiteOpenHelper implements DBConstants {

    public static final String TABLE_NAME = "message_details";
    public static final String DB_NAME = TABLE_NAME + ".db.sqlite";

    public MessageDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                DB_MESSAGE_UNID + " VARCHAR(30)," +
                DB_MESSAGE_TITLE + " TEXT," +
                DB_MESSAGE_CONTENT + " TEXT," +
                DB_MESSAGE_SENDER + " VARCHAR(100)," +
                DB_MESSAGE_TIME + " VARCHAR(100)," +
                DB_MESSAGE_INBOX_LABEL + " VARCHAR(10)" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Boolean checkIfExist(String uniqueId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_MESSAGE_UNID};
        String whereClause = DB_MESSAGE_UNID + " = ?;";
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

    public long insertDB(Message message) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_MESSAGE_UNID, message.getUniqueId());
        values.put(DB_MESSAGE_TITLE, message.getTitle());
        values.put(DB_MESSAGE_CONTENT, message.getContent());
        values.put(DB_MESSAGE_SENDER, message.getSender());
        values.put(DB_MESSAGE_TIME, message.getTime());
        values.put(DB_MESSAGE_INBOX_LABEL, message.getInboxLabel());

        long rowId = db.insert(TABLE_NAME, null, values);

        return rowId;
    }

    public int updateDB(Message message) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_MESSAGE_UNID, message.getUniqueId());
        values.put(DB_MESSAGE_TITLE, message.getTitle());
        values.put(DB_MESSAGE_CONTENT, message.getContent());
        values.put(DB_MESSAGE_SENDER, message.getSender());
        values.put(DB_MESSAGE_TIME, message.getTime());
        values.put(DB_MESSAGE_INBOX_LABEL, message.getInboxLabel());
        String whereClause = DB_MESSAGE_UNID + "='" + message.getUniqueId() + "'";

        int count = db.update(TABLE_NAME, values, whereClause, null);

        return count;
    }

    public ArrayList<Message> getAllMessageList() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Message> messageList = new ArrayList<Message>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String uniqueId = cursor.getString(1);
            String title = cursor.getString(2);
            String content = cursor.getString(3);
            String sender = cursor.getString(4);
            String time = cursor.getString(5);
            String inboxLabel = cursor.getString(6);
            Message message = new Message(uniqueId, title, content, sender, time, inboxLabel);
            messageList.add(message);
        }
        cursor.close();

        return messageList;
    }

    public void deleteByUniqueId(String uniqueId) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = DB_MESSAGE_UNID + " = ?;";
        String[] whereArgs = {uniqueId};
        db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public void closeDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }
}
