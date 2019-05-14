package com.example.s4swithtabs;

import java.util.Date;

public class EventModel {
    private String eventName;
    private String eventCreator;
    private long eventTime;

    public EventModel(String eventName, String eventCreator) {
        this.eventCreator = eventCreator;
        this.eventName= eventName;

        // Initialize to current time
       eventTime = new Date().getTime();
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventCreator() {
        return eventCreator;
    }

    public void setEventCreator(String eventCreator) {
        this.eventCreator = eventCreator;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
}
