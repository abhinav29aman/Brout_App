package com.adarshaaman.comestore;

public class Shop {
    private String name;
    private String category; //serves as entry to shops ,can't search shops now
    private int followers;
    private String addressLine1;
    private String addressLine2;
    private String contact;
    private String photourl;
    private double latitude ;
    private  double longitude;

    public Shop(){}
    public  Shop(String name, String category ,int followers, String addressLine1, String addressLine2, String contact,String photourl,double latitude, double longitude){
        this.name=name;
        this.category=category;
        this.followers=followers;
        this.addressLine1=addressLine1;
        this.addressLine2= addressLine2;
        this.contact= contact;
        this.photourl=photourl;
        this.latitude= latitude;
        this.longitude = longitude;


    }

    public String getName(){return  name;}

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

    public  String getCategory(){return  category;}

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getPhotourl() {
        return photourl;
    }

    public int getFollowers() {
        return followers;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}
