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
 * Class for handling message database read and write operations
 * <p>
 * This class is used to read and write message data objects in database
 *
 * @author Tsung Wei Ho
 * @version 0224.2017
 * @since 1.0.0
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

    /**
     * Initialize database
     *
     * @param db currently available database
     */
    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                DB_MESSAGE_UNID + " VARCHAR(30)," +
                DB_MESSAGE_TITLE + " TEXT," +
                DB_MESSAGE_CONTENT + " TEXT," +
                DB_MESSAGE_SENDER + " VARCHAR(100)," +
                DB_MESSAGE_SENDER_UID + " VARCHAR(100)," +
                DB_MESSAGE_TIME + " VARCHAR(100)," +
                DB_MESSAGE_INBOX_LABEL + " VARCHAR(50)" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Check if the instance already exists in database
     *
     * @param uniqueId the unique id of message object
     * @return boolean that indicate if the data instance exists in database
     */
    public Boolean isExist(String uniqueId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_MESSAGE_UNID};
        String whereClause = DB_MESSAGE_UNID + " = ?;";
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
     * @param message the message object to be inserted
     * @return the result of inserting message object
     */
    public long insertDB(Message message) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_MESSAGE_UNID, message.getUniqueId());
        values.put(DB_MESSAGE_TITLE, message.getTitle());
        values.put(DB_MESSAGE_CONTENT, message.getContent());
        values.put(DB_MESSAGE_SENDER, message.getSender());
        values.put(DB_MESSAGE_SENDER_UID, message.getSenderImg());
        values.put(DB_MESSAGE_TIME, message.getTime());
        values.put(DB_MESSAGE_INBOX_LABEL, message.getInboxLabel());

        long rowId = db.insert(TABLE_NAME, null, values);

        return rowId;
    }

    /**
     * Update existing instance in database
     *
     * @param message the message object to be updated
     * @return the row count of updated message objects
     */
    public int updateDB(Message message) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB_MESSAGE_UNID, message.getUniqueId());
        values.put(DB_MESSAGE_TITLE, message.getTitle());
        values.put(DB_MESSAGE_CONTENT, message.getContent());
        values.put(DB_MESSAGE_SENDER, message.getSender());
        values.put(DB_MESSAGE_SENDER_UID, message.getSenderImg());
        values.put(DB_MESSAGE_TIME, message.getTime());
        values.put(DB_MESSAGE_INBOX_LABEL, message.getInboxLabel());
        String whereClause = DB_MESSAGE_UNID + "='" + message.getUniqueId() + "'";

        int count = db.update(TABLE_NAME, values, whereClause, null);

        return count;
    }

    /**
     * Get all message instances with specific label as an arrayList
     *
     * @param label the specified label
     * @return an arrayList that contains specified label
     */
    public ArrayList<Message> getMessageListByLabel(String label) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Message> messageList = new ArrayList<Message>();

        if (label.equalsIgnoreCase(LABEL_MSG_STAR)) {
            messageList = getStarMessageList(db);
        } else {
            String sql = "SELECT * FROM " + TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                if (cursor.getString(7).split(SEPARATOR_MSG_LABEL)[2].equalsIgnoreCase(label)) {
                    String uniqueId = cursor.getString(1);
                    String title = cursor.getString(2);
                    String content = cursor.getString(3);
                    String sender = cursor.getString(4);
                    String senderImg = cursor.getString(5);
                    String time = cursor.getString(6);
                    String inboxLabel = cursor.getString(7);
                    Message message = new Message(uniqueId, title, content, sender, senderImg, time, inboxLabel);
                    messageList.add(message);
                }
            }
            cursor.close();
        }

        return messageList;
    }

    /**
     * Get all starred mails without those in trash box
     *
     * @param db currently available database
     * @return an arrayList that contains all starred mails that are not in trash box
     */
    private ArrayList<Message> getStarMessageList(SQLiteDatabase db) {
        ArrayList<Message> messageList = new ArrayList<Message>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(7).split(SEPARATOR_MSG_LABEL)[1].equalsIgnoreCase(LABEL_MSG_STAR)) {
                if (!cursor.getString(7).split(SEPARATOR_MSG_LABEL)[2].equalsIgnoreCase(LABEL_MSG_TRASH)) {
                    String uniqueId = cursor.getString(1);
                    String title = cursor.getString(2);
                    String content = cursor.getString(3);
                    String sender = cursor.getString(4);
                    String senderImg = cursor.getString(5);
                    String time = cursor.getString(6);
                    String inboxLabel = cursor.getString(7);
                    Message message = new Message(uniqueId, title, content, sender, senderImg, time, inboxLabel);
                    messageList.add(message);
                }
            }
        }
        cursor.close();

        return messageList;
    }

    /**
     * Get all unread messages as an arrayList
     *
     * @param currentMessageList the messageList to get unread messages from
     * @return an arrayList that contains all unread messages
     */
    public ArrayList<Message> getUnreadMessageListInBox(ArrayList<Message> currentMessageList) {
        ArrayList<Message> unreadMessageList = new ArrayList<>();
        for (int i = 0; i < currentMessageList.size(); i++) {
            if (currentMessageList.get(i).getInboxLabel().split(SEPARATOR_MSG_LABEL)[0].equalsIgnoreCase(LABEL_MSG_UNREAD)) {
                unreadMessageList.add(currentMessageList.get(i));
            }
        }
        return unreadMessageList;
    }

    /**
     * Change message label to read or unread
     *
     * @param message the message to be changed read/unread labels
     * @param isRead  boolean that indicate if the message has been read
     * @return the number of messages that have been alternated labels
     */
    public int markMailByLabel(Message message, String isRead) {
        String newInboxLabel = isRead + SEPARATOR_MSG_LABEL + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1] + SEPARATOR_MSG_LABEL
                + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[2] + SEPARATOR_MSG_LABEL + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[3];

        message.setInboxLabel(newInboxLabel);

        return updateDB(message);
    }

    /**
     * Change message label to starred or not starred
     *
     * @param message the message to be changed read/unread labels
     * @param isStar  boolean that indicate if the message is starred
     * @return the number of messages that have been alternated labels
     */
    public int starMailByLabel(Message message, String isStar) {
        String newInboxLabel = message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[0] + SEPARATOR_MSG_LABEL + isStar + SEPARATOR_MSG_LABEL
                + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[2] + SEPARATOR_MSG_LABEL + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[3];

        message.setInboxLabel(newInboxLabel);

        return updateDB(message);
    }

    /**
     * Change message directory between inbox and trash
     *
     * @param message the message to be changed read/unread labels
     * @param label   the label that indicate which mailbox message is in
     * @return the number of messages that have been alternated labels
     */
    public int moveDirByLabel(Message message, String label) {
        String newInboxLabel = message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[0] + SEPARATOR_MSG_LABEL +
                message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[1] + SEPARATOR_MSG_LABEL +
                label + SEPARATOR_MSG_LABEL + message.getInboxLabel().split(SEPARATOR_MSG_LABEL)[3];

        message.setInboxLabel(newInboxLabel);

        return updateDB(message);
    }

    /**
     * Get the message instance with specified uniqueId
     *
     * @param unId the specified uniqueId
     * @return the message object with uniqueId
     */
    public Message getMessageByUnId(String unId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {DB_MESSAGE_UNID, DB_MESSAGE_TITLE, DB_MESSAGE_CONTENT, DB_MESSAGE_SENDER, DB_MESSAGE_SENDER_UID, DB_MESSAGE_TIME, DB_MESSAGE_INBOX_LABEL};
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
            String senderImg = cursor.getString(4);
            String time = cursor.getString(5);
            String inboxLabel = cursor.getString(6);
            message = new Message(uniqueId, title, content, sender, senderImg, time, inboxLabel);
        }
        cursor.close();
        db.close();

        return message;
    }

    /**
     * Delete instance by specified uniqueId
     *
     * @param uniqueId the specified uniqueId
     */
    public void deleteByUniqueId(String uniqueId) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = DB_MESSAGE_UNID + " = ?;";
        String[] whereArgs = {uniqueId};
        db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    /**
     * Delete event database
     */
    public void deleteAllDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    /**
     * Close database connection
     */
    public void closeDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }
}
