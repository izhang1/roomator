package com.example.izhang.roomator;

/**
 * Created by izhang on 10/18/15.
 */
public class bills {
    private int cost;
    private String desc;
    private String ownerID;

    public bills(int cost, String desc, String ownerID){
        this.cost = cost;
        this.desc = desc;
        this.ownerID = ownerID;
    }

    public String getDesc(){
        return this.desc;
    }

    @Override
    public String toString(){
        return this.desc;
    }



}
