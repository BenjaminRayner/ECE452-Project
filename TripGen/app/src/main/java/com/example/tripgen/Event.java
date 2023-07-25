package com.example.tripgen;

public class Event {
    private int imageResource;
    private String name;
    private String description;
    private String startTime;
    private String endTime;
    private int startTimeHour;
    private int startTimeMin;
    private int endTimeHour;
    private int endTimeMin;

    public Event(int imageResource, String name, String description, int startTimeHour, int startTimeMin, int endTimeHour, int endTimeMin) {
        this.imageResource = imageResource;
        this.name = name;
        this.description = description;
        this.startTimeHour = startTimeHour;
        this.startTimeMin = startTimeMin;
        this.endTimeHour = endTimeHour;
        this.endTimeMin = endTimeMin;
    }


    public int getImageResource() {
        return imageResource;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
