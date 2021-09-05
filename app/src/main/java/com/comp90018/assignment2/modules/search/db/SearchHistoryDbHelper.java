package com.comp90018.assignment2.modules.search.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author xiaotian
 */
public class SearchHistoryDbHelper extends SQLiteOpenHelper {

    private static String name = "search_history.db";
    private static Integer version = 1;

    public SearchHistoryDbHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table,called Records with only one column name to store the history:
        db.execSQL("create table records(id integer primary key autoincrement,name varchar(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
