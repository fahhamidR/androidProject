package com.example.fahim.models;

public class Player {
    private long id;
    private long teamId;
    private String name;
    private String position;
    private String sportType;

    public Player(long id, long teamId, String name, String position, String sportType) {
        this.id = id;
        this.teamId = teamId;
        this.name = name;
        this.position = position;
        this.sportType = sportType;
    }

    public long getId() {
        return id;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getSportType() {
        return sportType;
    }
} 