package com.example.daniel.aroundme.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;


public class DBFabHandler {
    private  DBHelper helper ;


    public DBFabHandler(Context context) {
        helper = new DBHelper(context,Constants.DATABASE_FAVORITES,null,Constants.DATABASE_VERSION);
    }
    //This method is adding a new place to db
    public void addPlace(Place place){

        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_LAT, place.getLatitude());
            values.put(Constants.COLUMN_LNG,place.getLongitude());
            values.put(Constants.COLUMN_ID, place.getId());
            values.put(Constants.COLUMN_ICON, place.getIcon());
            values.put(Constants.COLUMN_NAME, place.getName());
            values.put(Constants.COLUMN_OPENING_HOURS, place.getOpening_hours());
            values.put(Constants.COLUMN_PHOTOS, place.getPhotos());
            values.put(Constants.COLUMN_PLACE_ID, place.getPlace_id());
            values.put(Constants.COLUMN_REFERENCE, place.getReference());
            values.put(Constants.COLUMN_TYPES, place.getTypes());
            values.put(Constants.COLUMN_VICINITY, place.getVicinity());


            db.insertOrThrow(Constants.TABLE_NAME, null, values);

        }catch (SQLiteException e){
            e.getMessage();
        }finally {
            if(db.isOpen())
                db.close();
        }


    }
    //This method is removing a location by getting it's id
    public void removeLocation(int id) {

        SQLiteDatabase db = helper.getWritableDatabase();
        try {

            db.delete(Constants.TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});

        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
            if (db.isOpen())
                db.close();


        }
    }
    //This method is removing all locations
    public void removeAllLocations(){

        SQLiteDatabase db = helper.getWritableDatabase();

        try {

            db.delete(Constants.TABLE_NAME, null, null);



        }catch (SQLiteException e){
            e.getMessage();
        }finally {
            if(db.isOpen())
                db.close();
        }
    }

    //This cursor method is go through db, and return all details
    public Cursor showAllLocations(){
        Cursor cursor = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        try {
            cursor = db.query(Constants.TABLE_NAME,null,null,null,null,null,null,null);
        }catch (SQLiteException e){
        e.printStackTrace();
        } //finally {
//            if(db.isOpen())
//                db.close();
//        }
        return cursor;
    }

//

    public ArrayList<Place> showAllLocationsArrayListAndSort(){
        ArrayList<Place> placeList = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        try {
            cursor = db.query(Constants.TABLE_NAME,null,null,null,null,null,Constants.COLUMN_NAME+" COLLATE NOCASE");
        }catch (SQLiteException e){
            e.printStackTrace();
        }

        if (cursor != null ) {
            while (cursor.moveToNext()) {



                Place place = new Place();
                place.setLatitude(cursor.getDouble(cursor.getColumnIndex(Constants.COLUMN_LAT)));
                place.setLongitude(cursor.getDouble(cursor.getColumnIndex(Constants.COLUMN_LNG)));
                place.setIcon(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_ICON)));
                place.setName(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_NAME)));
                place.setOpening_hours(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_OPENING_HOURS)));
                place.setPhotos(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PHOTOS)));
                place.setId(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PLACE_ID)));
                place.setReference(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_REFERENCE)));
                place.setTypes(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_TYPES)));
                place.setVicinity(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_VICINITY)));


                placeList.add(place);



            }
        }
        return placeList;
    }





}