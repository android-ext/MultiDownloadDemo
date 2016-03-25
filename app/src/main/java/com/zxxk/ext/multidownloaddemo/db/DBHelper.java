package com.zxxk.ext.multidownloaddemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 单例三步曲
 * Created by Ext on 2015/11/27.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "download.db";

    // 第二步
    private static DBHelper mDBHelper = null; // 静态对象引用

    private static final int VERSION = 1;
    private static final String SQL_CREATE = "create table thread_info(_id integer primary key autoincrement, thread_id integer, url text, start integer, end integer, finished integer)";
    private static final String SQL_DROP = "drop table if exists thread_info";

    // 第一步
    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * 第三步
     * 获得类的对象
     */
    public static DBHelper getInstance(Context context) {
        if (mDBHelper == null) {
            mDBHelper = new DBHelper(context);
        }
        return mDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}
