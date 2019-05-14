package com.example.s4swithtabs;

import java.util.Date;

public class EventModel {
    private String eventName;
    private String eventCreator;
    public String eventAdress;
    private long eventDate;
    private long eventTime;
    private String eventInfo;

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

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(String eventInfo) {
        this.eventInfo= eventInfo;
    }

    public String getEventAdress() {
        return eventAdress;
    }

    public void setEventAdress(String eventAdress) {
        this.eventAdress = eventAdress;
    }
}
