package com.example.s4swithtabs;

import java.util.ArrayList;

public class EventModel {
    public ArrayList<String> eventVisitors;
    private String eventName;
    private String eventCreator;
    private String eventAdress;
    private long eventTime;
    private String eventInfo;

    public EventModel() {
//        this.eventCreator = eventCreator;
//        this.eventName= eventName;
//        this.eventAdress = eventAdress;
//        this.eventTime = eventTime;
//        this.eventInfo = eventInfo;
//
//        // Initialize to current time
//       eventTime = new Date().getTime();

        this.eventVisitors = new ArrayList<String>();
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

    public String getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(String eventInfo) {
        this.eventInfo = eventInfo;
    }

    public String getEventAdress() {
        return eventAdress;
    }

    public void setEventAdress(String eventAdress) {
        this.eventAdress = eventAdress;
    }
}
