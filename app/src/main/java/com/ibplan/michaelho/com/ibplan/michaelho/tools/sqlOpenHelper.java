package com.ibplan.michaelho.com.ibplan.michaelho.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by MichaelHo on 2015/4/7.
 */
public class sqlOpenHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "tasksdb.sqlite";
    public static final int VERSION = 1;
    public static final String TABLENAME = "consumption_stat";
    public static final String ID = "Id";
    public static final String NAME = "Name";
    public static final String TODAY_STAT = "Today_stat";

    public sqlOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLENAME + "(" +
                ID + "INTEGER PROMARY KEY AUTOINCREMENT NOT NULL, " +
                NAME + " VARCHAR(15) CHARACTER SET utf8, " +
                TODAY_STAT + "VARCHAR(15) " + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
