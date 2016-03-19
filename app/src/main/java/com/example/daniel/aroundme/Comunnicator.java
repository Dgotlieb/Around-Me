package com.example.daniel.aroundme;

import com.example.daniel.aroundme.database.Place;

import java.util.ArrayList;

/**
 * Created by Daniel on 11/7/2015.
 */

//interface to communicate between fragments
public interface Comunnicator {

    public void locationPicker(double latitude, double longitude, String place_name,String vicinity,String photos);
    public void locationTabletPicker(double latitude, double longitude, String place_name,String vicinity,String photos);
    public void favourites(Place place);
    public void goToFavo(double lat, double lng);
    public void prefs(double lat, double lng);
    public void multiTab();
    public void multi(int y);
    public void streetView(double lat,double lng);
    public void recommended();
    public void permissionsAgreed();
    public void noPermission();
    public void defaultUnit(String unit);
    public void tabletDefaultUnit(String unit);



}
