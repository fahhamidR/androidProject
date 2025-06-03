package com.example.fahim.models;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private long id;
    private String name;
    private String description;
    private String sportType;
    private String captain;
    private List<Player> players;

    public Team(long id, String name, String sportType, String description) {
        this.id = id;
        this.name = name;
        this.sportType = sportType;
        this.description = description;
        this.players = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSportType() {
        return sportType;
    }

    public String getCaptain() {
        return captain;
    }

    public void setCaptain(String captain) {
        this.captain = captain;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        players.add(player);
    }
} 