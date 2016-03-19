package com.example.daniel.aroundme.database;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.example.daniel.aroundme.activities_and_fragments.PrefrencesFragment;

/**
 * Created by Daniel on 11/28/2015.
 */
public class Place implements Parcelable {


    private double longitude;
    private double latitude;
    private String id;
    private String icon;
    private String name;
    private String opening_hours;
    private String photos;
    private String place_id;
    private String reference;
    private String types;
    private String vicinity;
    private int distance_from_user;
    private double distance_km ;
    private double distance_miles ;
    private String userChoice;

    public boolean isKM() {
        return isKM;
    }

    public void setIsKM(boolean isKM) {
        this.isKM = isKM;
    }

    private boolean isKM = true;


    private Double destinationDistance;


    public Place() {
    }

    public Place(String name, String photos) {
        this.name = name;
        this.photos = photos;
    }

    public Place(double longitude, double latitude, String id, String name, String opening_hours, String photos, String place_id, String reference, String types, String vicinity) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        this.name = name;
        this.opening_hours = opening_hours;
        this.photos = photos;
        this.place_id = place_id;
        this.reference = reference;
        this.types = types;
        this.vicinity = vicinity;
    }

    public Place(double latitude, double longitude, String id, String icon, String name, String opening_hours, String photos, String place_id, String reference, String types, String vicinity) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.icon = icon;
        this.id = id;
        this.name = name;
        this.opening_hours = opening_hours;
        this.photos = photos;
        this.place_id = place_id;
        this.reference = reference;
        this.types = types;
        this.vicinity = vicinity;

    }

    protected Place(Parcel in) {
        longitude = in.readDouble();
        latitude = in.readDouble();
        id = in.readString();
        icon = in.readString();
        name = in.readString();
        opening_hours = in.readString();
        photos = in.readString();
        place_id = in.readString();
        reference = in.readString();
        types = in.readString();
        vicinity = in.readString();
    }

    //getting the distance from user
    public void setDistanceFromUser(double lat, double lng){

        try {
            Location loc = new Location("a");

            loc.setLatitude(lat);
            loc.setLongitude(lng);

            Location location_b = new Location("b");
            location_b.setLatitude(latitude);
            location_b.setLongitude(longitude);

            distance_km = (int) loc.distanceTo(location_b) / 1000;
            distance_miles = (int) loc.distanceTo(location_b) / 1000 * 1.60934;

        }catch (Exception e){
            distance_km = 0;
            distance_miles = 0;
        }

    }

    public double getDistance_km() {
        return distance_km;
    }

    public void setDistance_km(double distance_km) {
        this.distance_km = distance_km;
    }

    public double getDistance_miles() {
        return distance_miles;
    }

    public void setDistance_miles(double distance_miles) {
        this.distance_miles = distance_miles;
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public int getDistance_from_user() {
        return distance_from_user;
    }

    public void setDistance_from_user(int distance_from_user) {
        this.distance_from_user = distance_from_user;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(String opening_hours) {
        this.opening_hours = opening_hours;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getDestinationDistance() {
        return destinationDistance;
    }

    public void setDestinationDistance(Double destinationDistance) {
        this.destinationDistance = destinationDistance;
    }



    @Override
    public String toString() {


        String msg = getName() +'\n'+ getVicinity()+'\n' ;
        if(isKM)
            msg+= "Distance: "+ distance_km+ " KM" ;
        else
            msg += "Distance: " + (int)distance_miles+ " Miles";

        return msg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(id);
        dest.writeString(icon);
        dest.writeString(name);
        dest.writeString(opening_hours);
        dest.writeString(photos);
        dest.writeString(place_id);
        dest.writeString(reference);
        dest.writeString(types);
        dest.writeString(vicinity);
    }
}
