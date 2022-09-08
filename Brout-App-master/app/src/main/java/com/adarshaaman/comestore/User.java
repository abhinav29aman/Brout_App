package com.adarshaaman.comestore;

public class User {
    private String contact;
    private double latitude;
    private  double longitude;

public User(){ this.latitude =  15.85456;}
    public User(String contact,double latitude,double longitude){
        this.contact=contact;
        this.latitude = latitude;
        this.longitude = longitude;

    }
    public String getContact (){return contact;}

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
