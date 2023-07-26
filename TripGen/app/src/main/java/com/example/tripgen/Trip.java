package com.example.tripgen;

import java.util.ArrayList;

public class Trip {
    long startDate;
    long endDate;
    String owner;
    String name;
    ArrayList<String> sharedWith;

    int transportationBudget = 0;
    int accommodationBudget = 0;
    int foodBudget = 0;
    int activityBudget = 0;
    int transportationExpenses = 0;
    int accommodationExpenses = 0;
    int foodExpenses = 0;
    int activityExpenses = 0;

    public Trip() {}

    public Trip(long startDate, long endDate, String owner, String name, ArrayList<String> sharedWith) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.owner = owner;
        this.name  = name;
        this.sharedWith = sharedWith;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
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

    public int getTransportationBudget() {
        return transportationBudget;
    }

    public int getAccommodationBudget() {
        return accommodationBudget;
    }

    public int getFoodBudget() {
        return foodBudget;
    }

    public int getActivityBudget() {
        return activityBudget;
    }

    public int getTransportationExpenses() {
        return transportationExpenses;
    }

    public int getAccommodationExpenses() {
        return accommodationExpenses;
    }

    public int getFoodExpenses() {
        return foodExpenses;
    }

    public int getActivityExpenses() {
        return activityExpenses;
    }

}
