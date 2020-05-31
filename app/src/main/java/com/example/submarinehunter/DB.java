package com.example.submarinehunter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class DB {

    private SQLiteDatabase db;

    private static final String TABLE_NAME = "record";
    private static final String TABLE_ROW_ID = "_id";
    private static final String TABLE_ROW_RESULT = "result";
    private static final String TABLE_ROW_TSM = "tsm";

    private static final String DB_NAME = "submarine_db";
    private static final int DB_VERSION = 1;

    public DB(Context context) {
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public void insert(int result) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_ROW_RESULT, result);
        contentValues.put(TABLE_ROW_TSM, System.currentTimeMillis());

        db.insert(TABLE_NAME, null, contentValues);
    }

    public ArrayList<Record> getAll(){
        ArrayList<Record> list = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "result ASC");
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(TABLE_ROW_ID);
            int resultIndex = cursor.getColumnIndex(TABLE_ROW_RESULT);
            int tsmIndex = cursor.getColumnIndex(TABLE_ROW_TSM);
            do {
                int id = Integer.parseInt(cursor.getString(idIndex));
                int result = Integer.parseInt(cursor.getString(resultIndex));
                long tsm = Long.parseLong(cursor.getString(tsmIndex));

                list.add(new Record(id, result, tsm));
            } while (cursor.moveToNext());
        }

        return list;
    }

    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
        public CustomSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String newTableMatchQuery = "create table "
                    + TABLE_NAME + " ("
                    + TABLE_ROW_ID + " integer primary key autoincrement not null, "
                    + TABLE_ROW_RESULT + " integer not null,"
                    + TABLE_ROW_TSM + " int8 not null)";
            db.execSQL(newTableMatchQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }
}
