package com.example.tripgen;

public class Activity {
    String name;
    int startTime_abs;
    int startTimeHour;
    int startTimeMin;
    int endTimeHour;
    int endTimeMin;

    Activity() {}
    Activity(String name, int startTime_abs, int startTimeHour, int startTimeMin, int endTimeHour, int endTimeMin) {
        this.name = name;
        this.startTime_abs = startTime_abs;
        this.startTimeHour = startTimeHour;
        this.startTimeMin = startTimeMin;
        this.endTimeHour = endTimeHour;
        this.endTimeMin = endTimeMin;
    }

    public int getEndTimeHour() {
        return endTimeHour;
    }

    public int getEndTimeMin() {
        return endTimeMin;
    }

    public int getStartTime_abs() {
        return startTime_abs;
    }

    public int getStartTimeHour() {
        return startTimeHour;
    }

    public int getStartTimeMin() {
        return startTimeMin;
    }

    public String getName() {
        return name;
    }
}
