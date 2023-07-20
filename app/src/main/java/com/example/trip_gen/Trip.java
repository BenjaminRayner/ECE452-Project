package com.example.trip_gen;

import java.util.ArrayList;

public class Trip {
    String startDate;
    String endDate;
    String owner;
    String name;
    ArrayList<String> sharedWith;

    public Trip() {}

    public Trip(String startDate, String endDate, String owner, String name, ArrayList<String> sharedWith) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.owner = owner;
        this.name  = name;
        this.sharedWith = sharedWith;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getSharedWith() {
        return sharedWith;
    }
}
