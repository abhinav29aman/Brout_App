package com.adarshaaman.comestore;

public class Inventory {
    private String title;
    private String description;
    private int price ;
   private boolean isInStock;
   private String returnPolicy ;
    private int likes;
    private String imageurl;
    private String shopId;
    private String key ;
    private boolean isAd;
   private  int orders;
    private boolean isService;


    public Inventory(){}
    public Inventory(String title,String description ,int price  , int likes,String imageurl,String shopId,String key,boolean isAd,int orders,boolean isInStock,String returnPolicy,boolean isService){
        this.title= title;
        this.description= description;
        this.price= price;
        this.isService = isService;
        this.likes=likes;
        this.imageurl = imageurl;
        this.shopId= shopId;
        this.key= key;
        this.isAd = isAd;
        this.orders = orders;
        this.returnPolicy = returnPolicy;
        this.isInStock = isInStock;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public boolean isInStock() {
        return isInStock;
    }

    public String getReturnPolicy() {
        return returnPolicy;
    }

    public boolean isService() {
        return isService;
    }

    public void setService(boolean service) {
        isService = service;
    }

    public void setInStock(boolean inStock) {
        isInStock = inStock;
    }

    public void setReturnPolicy(String returnPolicy) {
        this.returnPolicy = returnPolicy;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getKey() {
        return key;
    }

    public boolean isAd() {
        return isAd;
    }

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }

    public void setAd(boolean add) {
        isAd = add;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getLikes() {
        return likes;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getShopId() {
        return shopId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
