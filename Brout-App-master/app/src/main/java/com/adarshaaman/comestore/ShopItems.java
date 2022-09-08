package com.adarshaaman.comestore;

import java.util.ArrayList;

public class ShopItems {
    private ArrayList<Inventory> inventories = new ArrayList<Inventory>();

    public ShopItems(ArrayList<Inventory> inventories){this.inventories=inventories;}

    public ArrayList<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(ArrayList<Inventory> inventories) {
        this.inventories = inventories;
    }
}
