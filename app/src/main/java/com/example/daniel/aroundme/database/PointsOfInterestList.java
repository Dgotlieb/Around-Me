package com.example.daniel.aroundme.database;

/**
 * Created by Daniel on 3/19/2016.
 */
public class PointsOfInterestList {

    private String point;
    private int image;


    public PointsOfInterestList() {
    }

    public PointsOfInterestList(String point, int image) {
        this.point = point;
        this.image = image;
    }


    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
