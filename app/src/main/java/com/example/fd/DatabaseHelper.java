package com.example.fd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="caretaker.db";
    public static final String TABLE_NAME ="caretaker_table";
    public static final String COLUMN_1 ="id";
    public static final String COLUMN_2 ="caretakername";
    public static final String COLUMN_3 ="caretakerphone";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, caretakername TEXT, caretakerphone INTEGER) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String cname, String cphone){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_2, cname);
        contentValues.put(COLUMN_3, cphone);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor fetchData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

}