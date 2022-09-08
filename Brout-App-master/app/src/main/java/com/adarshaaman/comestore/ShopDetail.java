package com.adarshaaman.comestore;

public class ShopDetail {
    private String timeDetail;
    private  String detail;
    public  ShopDetail (String timeDetail,String detail){this.timeDetail=timeDetail;

    this.detail= detail;}
    public ShopDetail (){}

    public String getTimeDetail(){
        return  timeDetail;

    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setTimeDetail(String timeDetail) {
        this.timeDetail = timeDetail;
    }
}
