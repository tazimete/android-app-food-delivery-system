package com.example.smartsend.smartsendapp;

/**
 * Created by AGM TAZIM on 1/4/2016.
 */
public class Outlet {
    int id;
    String name;
    int type;

    //type = 1 ; client
    //type = 2 ; outlet

    //Constructor
    public Outlet(){

    }

    //Constructor
    public Outlet(int id, String name, int type){
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
