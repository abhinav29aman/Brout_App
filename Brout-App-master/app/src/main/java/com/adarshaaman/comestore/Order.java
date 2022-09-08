package com.adarshaaman.comestore;

public class Order {
    private String inventoryKey;
    private String itemName;
    private Shop shop;
    private int totalPrice;
    private int number;
    private String status;
    private String riderId;
    private int code;
    private User user;
    private String orderKey;
    private String returnPolicy;
    private String userToken;
    private String shopToken;
     private String riderToken;
     private  int type ;

    public Order() {
    }

    public Order(String inventoryKey, String itemName, Shop shop, int totalPrice, int number, String status, String riderId, int code, User user, String orderKey,String returnPolicy,String userToken, String shopToken, String riderToken, int type) {
        this.inventoryKey = inventoryKey;
        this.itemName = itemName;
        this.shop = shop;
        this.totalPrice = totalPrice;
        this.number = number;
        this.status = status;
        this.riderId = riderId;
        this.code = code;
        this.user = user;
        this.orderKey = orderKey;
        this.returnPolicy = returnPolicy;
        this.userToken = userToken;
        this.shopToken = shopToken;
        this.riderToken = riderToken;
        this.type = type;
    }

    public String getInventoryKey() {
        return inventoryKey;
    }

    public void setInventoryKey(String inventoryKey) {
        this.inventoryKey = inventoryKey;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }

    public void setReturnPolicy(String returnPolicy) {
        this.returnPolicy = returnPolicy;
    }

    public String getReturnPolicy() {
        return returnPolicy;
    }

    public String getRiderToken() {
        return riderToken;
    }

    public String getShopToken() {
        return shopToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setRiderToken(String riderToken) {
        this.riderToken = riderToken;
    }

    public void setShopToken(String shopToken) {
        this.shopToken = shopToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}