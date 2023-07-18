package com.example.tripgen;

public class Event {
    private int imageResource;
    private String name;
    private String description;
    private String startTime;
    private String endTime;

    public Event(int imageResource, String name, String description, String startTime, String endTime) {
        this.imageResource = imageResource;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
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
