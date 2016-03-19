package com.example.daniel.aroundme.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class DBFabHelper extends SQLiteOpenHelper {
    public DBFabHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
        //This method creating the db according to the values below (table name, columns,type..)
    @Override
    public void onCreate(SQLiteDatabase db) {

        String favorites = "CREATE TABLE places (_id INTEGER PRIMARY KEY, latitude TEXT, longitude TEXT, id TEXT, icon TEXT, name TEXT, opening_hours TEXT, photos TEXT, place_id TEXT, reference TEXT, types TEXT, vicinity TEXT ); ";

        try {
            db.execSQL(favorites);
        }catch (SQLiteException e){
            e.getMessage();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

