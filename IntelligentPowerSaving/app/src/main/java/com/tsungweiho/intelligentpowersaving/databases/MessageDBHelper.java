package com.tsungweiho.intelligentpowersaving.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tsungweiho.intelligentpowersaving.constants.DBConstants;
import com.tsungweiho.intelligentpowersaving.constants.PubNubAPIConstants;
import com.tsungweiho.intelligentpowersaving.objects.Message;

import java.util.ArrayList;

/**
 * Created by tsung on 2017/2/24.
 */

public class MessageDBHelper extends SQLiteOpenHelper implements DBConstants, PubNubAPIConstants {

    private static final String TABLE_NAME = "message_details";
    private static final String DB_NAME = TABLE_NAME + ".db.sqlite";

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
        Boolean ifExist = cursor.getCount() != 0;

        cursor.close();
        db.close();

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

    // Box label is recorded in the second label of inboxLabel
    public ArrayList<Message> getMessageListByLabel(String label) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Message> messageList = new ArrayList<Message>();

        if (label.equalsIgnoreCase(LABEL_MSG_STAR)){
            messageList = getStarMessageList(db);
        } else {
            String sql = "SELECT * FROM " + TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                if (cursor.getString(6).split(SEPARATOR_MSG_LABEL)[2].equalsIgnoreCase(label)) {
                    String uniqueId = cursor.getString(1);
                    String title = cursor.getString(2);
                    String content = cursor.getString(3);
                    String sender = cursor.getString(4);
                    String time = cursor.getString(5);
                    String inboxLabel = cursor.getString(6);
                    Message message = new Message(uniqueId, title, content, sender, time, inboxLabel);
                    messageList.add(message);
                }
            }
            cursor.close();
        }

        return messageList;
    }

    // Get all starred mails without those in trash box
    private ArrayList<Message> getStarMessageList(SQLiteDatabase db){
        ArrayList<Message> messageList = new ArrayList<Message>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(6).split(SEPARATOR_MSG_LABEL)[1].equalsIgnoreCase(LABEL_MSG_STAR)) {
                if (!cursor.getString(6).split(SEPARATOR_MSG_LABEL)[2].equalsIgnoreCase(LABEL_MSG_TRASH)){
                    String uniqueId = cursor.getString(1);
                    String title = cursor.getString(2);
                    String content = cursor.getString(3);
                    String sender = cursor.getString(4);
                    String time = cursor.getString(5);
                    String inboxLabel = cursor.getString(6);
                    Message message = new Message(uniqueId, title, content, sender, time, inboxLabel);
                    messageList.add(message);
                }
            }
        }
        cursor.close();

        return messageList;
    }

    public ArrayList<Message> getUnreadMessageListInBox(ArrayList<Message> currentMessageList) {
        ArrayList<Message> unreadMessageList = new ArrayList<>();
        for (int i = 0; i < currentMessageList.size(); i++) {
            if (currentMessageList.get(i).getInboxLabel().split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(LABEL_MSG_UNREAD)) {
                unreadMessageList.add(currentMessageList.get(i));
            }
        }
        return unreadMessageList;
    }

    // Handle un/read of mails
    public int markMailByLabel(Message message, String ifRead) {
        SQLiteDatabase db = getWritableDatabase();

        String newInboxLabel = ifRead + SEPARATOR_MSG_LABEL + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1] + SEPARATOR_MSG_LABEL
                + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[2] + SEPARATOR_MSG_LABEL + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[3];

        ContentValues values = new ContentValues();
        values.put(DB_MESSAGE_UNID, message.getUniqueId());
        values.put(DB_MESSAGE_TITLE, message.getTitle());
        values.put(DB_MESSAGE_CONTENT, message.getContent());
        values.put(DB_MESSAGE_SENDER, message.getSender());
        values.put(DB_MESSAGE_TIME, message.getTime());
        values.put(DB_MESSAGE_INBOX_LABEL, newInboxLabel);
        String whereClause = DB_MESSAGE_UNID + "='" + message.getUniqueId() + "'";

        int count = db.update(TABLE_NAME, values, whereClause, null);

        return count;
    }

    // Handle star mails
    public int starMailByLabel(Message message, String ifStar) {
        SQLiteDatabase db = getWritableDatabase();

        String newInboxLabel = message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[0] + SEPARATOR_MSG_LABEL + ifStar + SEPARATOR_MSG_LABEL
                + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[2] + SEPARATOR_MSG_LABEL + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[3];

        ContentValues values = new ContentValues();
        values.put(DB_MESSAGE_UNID, message.getUniqueId());
        values.put(DB_MESSAGE_TITLE, message.getTitle());
        values.put(DB_MESSAGE_CONTENT, message.getContent());
        values.put(DB_MESSAGE_SENDER, message.getSender());
        values.put(DB_MESSAGE_TIME, message.getTime());
        values.put(DB_MESSAGE_INBOX_LABEL, newInboxLabel);
        String whereClause = DB_MESSAGE_UNID + "='" + message.getUniqueId() + "'";

        int count = db.update(TABLE_NAME, values, whereClause, null);

        return count;
    }

    // Handle mails moving between inbox and trash
    public int moveToBoxByLabel(Message message, String label) {
        SQLiteDatabase db = getWritableDatabase();

        String newInboxLabel = message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[0] + SEPARATOR_MSG_LABEL +
                message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1] + SEPARATOR_MSG_LABEL +
                label + SEPARATOR_MSG_LABEL + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[3];

        ContentValues values = new ContentValues();
        values.put(DB_MESSAGE_UNID, message.getUniqueId());
        values.put(DB_MESSAGE_TITLE, message.getTitle());
        values.put(DB_MESSAGE_CONTENT, message.getContent());
        values.put(DB_MESSAGE_SENDER, message.getSender());
        values.put(DB_MESSAGE_TIME, message.getTime());
        values.put(DB_MESSAGE_INBOX_LABEL, newInboxLabel);
        String whereClause = DB_MESSAGE_UNID + "='" + message.getUniqueId() + "'";

        int count = db.update(TABLE_NAME, values, whereClause, null);

        return count;
    }

    public Message getMessageByUnId(String unId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_MESSAGE_UNID, DB_MESSAGE_TITLE, DB_MESSAGE_CONTENT, DB_MESSAGE_SENDER, DB_MESSAGE_TIME, DB_MESSAGE_INBOX_LABEL};
        String whereClause = DB_MESSAGE_UNID + " = ?;";
        String[] whereArgs = {unId};
        Cursor cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs,
                null, null, null);
        Message message = null;
        while (cursor.moveToNext()) {
            String uniqueId = cursor.getString(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            String sender = cursor.getString(3);
            String time = cursor.getString(4);
            String inboxLabel = cursor.getString(5);
            message = new Message(uniqueId, title, content, sender, time, inboxLabel);
        }
        cursor.close();
        db.close();
        return message;
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
