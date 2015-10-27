package com.example.izhang.roomator;

/** Author: Ivan Zhang
 *  Company: NovusApp.com
 *  Application: Roomator
 *
 *  File: bills.java
 * --Class that has the information for the bills including the owner account id, cost per person, description of the bill.
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
