package com.example.fahim.models;

import java.util.Date;

public class EventModel {
    private long id;
    private String title;
    private String description;
    private String location;
    private String eventDate;
    private String eventTime;
    private String sportType;
    private String organizer;
    private int maxParticipants;
    private int currentParticipants;

    public EventModel(long id, String title, String description, String location,
                     String eventDate, String eventTime, String sportType,
                     int maxParticipants, int currentParticipants) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.sportType = sportType;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getSportType() {
        return sportType;
    }

    public String getOrganizer() {
        return organizer;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getCurrentParticipants() {
        return currentParticipants;
    }

    public boolean isFull() {
        return currentParticipants >= maxParticipants;
    }

    public String getFormattedDateTime() {
        return eventDate + " " + eventTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }
} 