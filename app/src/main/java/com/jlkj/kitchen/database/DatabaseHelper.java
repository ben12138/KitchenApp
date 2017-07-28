package com.jlkj.kitchen.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by benrui on 2017/7/5.
 * 创建本地数据库的对应的表
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_USER_TABLE = "create table user(id integer primary key autoincrement,userid integer(10),username varchar(30),password varchar(30)," +
            "nickname varchar(30),imgurl varchar(30),email varchar(30),birthday varchar(30),sex integer(1),company varchar(2000),introduction varchar(2000))";

    private Context context;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
