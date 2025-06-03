package com.example.fahim.controllers;

import android.content.Context;
import com.example.fahim.models.Team;
import com.example.fahim.models.Player;
import java.util.List;

public class TeamController extends BaseController {
    public TeamController(Context context, boolean isAdmin) {
        super(context, isAdmin);
    }

    // Admin operations
    public long createTeam(Team team) {
        checkAdminAccess();
        if (team == null || team.getName() == null || team.getSportType() == null) {
            return -1;
        }
        return dbHelper.createTeam(team);
    }

    public boolean updateTeam(Team team) {
        checkAdminAccess();
        if (team == null || team.getId() <= 0) {
            return false;
        }
        return dbHelper.updateTeam(team) > 0;
    }

    public boolean deleteTeam(long teamId) {
        checkAdminAccess();
        if (teamId <= 0) {
            return false;
        }
        return dbHelper.deleteTeam(teamId);
    }

    public long addPlayer(Player player) {
        checkAdminAccess();
        if (player == null || player.getTeamId() <= 0) {
            return -1;
        }
        
        // Check if team already has 11 players
        Team team = dbHelper.getTeam(player.getTeamId());
        if (team != null && team.getPlayers().size() >= 11) {
            return -1;
        }
        
        return dbHelper.createPlayer(player);
    }

    public boolean updatePlayer(Player player) {
        checkAdminAccess();
        if (player == null || player.getId() <= 0) {
            return false;
        }
        return dbHelper.updatePlayer(player) > 0;
    }

    public boolean deletePlayer(long playerId) {
        checkAdminAccess();
        if (playerId <= 0) {
            return false;
        }
        try {
            dbHelper.deletePlayer(playerId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // User operations (read-only)
    public List<Team> getAllTeams() {
        checkUserAccess();
        return dbHelper.getAllTeams();
    }

    public List<Team> getTeamsBySport(String sportType) {
        checkUserAccess();
        if (sportType == null || sportType.trim().isEmpty()) {
            return null;
        }
        return dbHelper.getTeamsBySport(sportType.trim());
    }

    public Team getTeamById(long teamId) {
        checkUserAccess();
        if (teamId <= 0) {
            return null;
        }
        return dbHelper.getTeamById((int)teamId);
    }

    public List<Player> getTeamPlayers(long teamId) {
        checkUserAccess();
        if (teamId <= 0) {
            return null;
        }
        return dbHelper.getTeamPlayers(teamId);
    }
} 