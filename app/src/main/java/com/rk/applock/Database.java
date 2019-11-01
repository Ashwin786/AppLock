package com.rk.applock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by user1 on 20/10/17.
 */
public class Database extends SQLiteOpenHelper {
    private static final int version = 1;
    private static final String dbname = "APPLOCK.db";
    private static Database dbhelper;
    private static Context context;
    private final SQLiteDatabase db;
    private String table_name = "APP_TABLE";
    private String CREATE_APP_TABLE = "CREATE TABLE " + table_name + " ( package_name VARCHAR(250) )";

    public Database(Context context) {
        super(context, dbname, null, version);
        db = getWritableDatabase();
    }

    public static Database getInstance(Context con) {
        if (dbhelper == null)
            dbhelper = new Database(con);
        context = con;
        return dbhelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    protected void insert_data(ArrayList<String> packageName_list) {
        for (int i = 0; i < packageName_list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("package_name", packageName_list.get(i));
            db.insertWithOnConflict(table_name, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public ArrayList<String> get_apps_name() {
        ArrayList<String> packageName_list = new ArrayList<>();
        String selectquery = "select * from " + table_name;
        Cursor cursor = db.rawQuery(selectquery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                packageName_list.add(cursor.getString(cursor.getColumnIndex("package_name")));
            } while (cursor.moveToNext());
        }
        return packageName_list;
    }

    public void delete_data() {
        String query = "Delete from " + table_name;
        db.execSQL(query);
    }
}
