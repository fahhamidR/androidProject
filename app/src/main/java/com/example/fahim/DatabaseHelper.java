package com.example.fahim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.fahim.models.Team;
import com.example.fahim.models.Player;
import com.example.fahim.models.NewsModel;
import com.example.fahim.models.EventModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SportsApp.db";
    public static final int DATABASE_VERSION = 2;

    // User Table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";

    // News Table
    public static final String TABLE_NEWS = "news";
    public static final String COL_NEWS_ID = "id";
    public static final String COL_NEWS_TITLE = "title";
    public static final String COL_NEWS_DESC = "description";
    public static final String COL_NEWS_DATE = "date";

    // Teams Table
    public static final String TABLE_TEAMS = "teams";
    public static final String COL_TEAM_ID = "id";
    public static final String COL_TEAM_NAME = "name";
    public static final String COL_TEAM_DESC = "description";
    public static final String COL_TEAM_SPORT = "sport_type";
    public static final String COL_TEAM_CAPTAIN = "captain";
    public static final String COL_TEAM_MEMBERS = "members";

    // Players Table
    public static final String TABLE_PLAYERS = "players";
    public static final String COL_PLAYER_ID = "id";
    public static final String COL_PLAYER_TEAM_ID = "team_id";
    public static final String COL_PLAYER_NAME = "name";
    public static final String COL_PLAYER_POSITION = "position";

    // Event table constants
    private static final String TABLE_EVENTS = "events";
    private static final String COL_EVENT_ID = "id";
    private static final String COL_EVENT_TITLE = "title";
    private static final String COL_EVENT_DESC = "description";
    private static final String COL_EVENT_LOCATION = "location";
    private static final String COL_EVENT_DATE = "event_date";
    private static final String COL_EVENT_TIME = "event_time";
    private static final String COL_EVENT_SPORT_TYPE = "sport_type";
    private static final String COL_EVENT_MAX_PARTICIPANTS = "max_participants";
    private static final String COL_EVENT_CURRENT_PARTICIPANTS = "current_participants";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_EMAIL + " TEXT PRIMARY KEY, " +
                COL_USER_PASSWORD + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_NEWS + " (" +
                COL_NEWS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NEWS_TITLE + " TEXT, " +
                COL_NEWS_DESC + " TEXT, " +
                COL_NEWS_DATE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_TEAMS + " (" +
                COL_TEAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TEAM_NAME + " TEXT, " +
                COL_TEAM_DESC + " TEXT, " +
                COL_TEAM_SPORT + " TEXT, " +
                COL_TEAM_CAPTAIN + " TEXT, " +
                COL_TEAM_MEMBERS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_PLAYERS + " (" +
                COL_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PLAYER_TEAM_ID + " INTEGER, " +
                COL_PLAYER_NAME + " TEXT, " +
                COL_PLAYER_POSITION + " TEXT, " +
                "FOREIGN KEY(" + COL_PLAYER_TEAM_ID + ") REFERENCES " + TABLE_TEAMS + "(" + COL_TEAM_ID + ")" + ")");

        // Create events table
        String createEventsTable = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COL_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EVENT_TITLE + " TEXT NOT NULL, " +
                COL_EVENT_DESC + " TEXT, " +
                COL_EVENT_LOCATION + " TEXT NOT NULL, " +
                COL_EVENT_DATE + " TEXT NOT NULL, " +
                COL_EVENT_TIME + " TEXT NOT NULL, " +
                COL_EVENT_SPORT_TYPE + " TEXT NOT NULL, " +
                COL_EVENT_MAX_PARTICIPANTS + " INTEGER NOT NULL, " +
                COL_EVENT_CURRENT_PARTICIPANTS + " INTEGER DEFAULT 0)";
        db.execSQL(createEventsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        
        // Create tables again
        onCreate(db);
    }

    // USER METHODS
    public boolean registerUser(String email, String password) {
        if (checkEmailExists(email)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_USER_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD + " = ?", new String[]{email, password});
        boolean success = cursor.getCount() > 0;
        cursor.close();
        return success;
    }

    public boolean isAdmin(String email) {
        return "admin@baust.edu".equals(email);
    }

    // NEWS METHODS
    public void addNews(String title, String desc, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NEWS_TITLE, title);
        cv.put(COL_NEWS_DESC, desc);
        cv.put(COL_NEWS_DATE, date);
        db.insert(TABLE_NEWS, null, cv);
        db.close();
    }

    public boolean deleteNews(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NEWS, COL_NEWS_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<NewsModel> getAllNews() {
        List<NewsModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NEWS + " ORDER BY " + COL_NEWS_ID + " DESC", null);
        while (cursor.moveToNext()) {
            list.add(new NewsModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_NEWS_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NEWS_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NEWS_DESC)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NEWS_DATE))
            ));
        }
        cursor.close();
        db.close();
        return list;
    }

    // TEAM METHODS
    public long createTeam(Team team) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TEAM_NAME, team.getName());
        cv.put(COL_TEAM_DESC, team.getDescription());
        cv.put(COL_TEAM_SPORT, team.getSportType());
        long teamId = db.insert(TABLE_TEAMS, null, cv);
        db.close();
        return teamId;
    }

    public Team getTeam(long teamId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TEAMS, null, COL_TEAM_ID + "=?",
                new String[]{String.valueOf(teamId)}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            Team team = new Team(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_TEAM_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_SPORT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_DESC))
            );
            team.setPlayers(getTeamPlayers(teamId));
            cursor.close();
            db.close();
            return team;
        }
        return null;
    }

    public int updateTeam(Team team) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TEAM_NAME, team.getName());
        cv.put(COL_TEAM_DESC, team.getDescription());
        cv.put(COL_TEAM_SPORT, team.getSportType());
        int rows = db.update(TABLE_TEAMS, cv, COL_TEAM_ID + "=?", 
                new String[]{String.valueOf(team.getId())});
        db.close();
        return rows;
    }

    public boolean deleteTeam(long teamId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // First delete all players associated with this team
            db.delete(TABLE_PLAYERS, COL_PLAYER_TEAM_ID + " = ?", new String[]{String.valueOf(teamId)});
            
            // Then delete the team
            int result = db.delete(TABLE_TEAMS, COL_TEAM_ID + " = ?", new String[]{String.valueOf(teamId)});
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Team> getTeamsBySport(String sportType) {
        List<Team> teams = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEAMS + " WHERE " + COL_TEAM_SPORT + "=?", new String[]{sportType});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TEAM_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_NAME));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_DESC));
            String sport = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_SPORT));

            Team team = new Team(id, name, sport, desc);
            team.setPlayers(getTeamPlayers(id));
            teams.add(team);
        }

        cursor.close();
        db.close();
        return teams;
    }

    // PLAYER METHODS
    public long createPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PLAYER_TEAM_ID, player.getTeamId());
        values.put(COL_PLAYER_NAME, player.getName());
        values.put(COL_PLAYER_POSITION, player.getPosition());
        long playerId = db.insert(TABLE_PLAYERS, null, values);
        db.close();
        return playerId;
    }

    public List<Player> getTeamPlayers(long teamId) {
        List<Player> players = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PLAYERS + " WHERE " + COL_PLAYER_TEAM_ID + "=?", new String[]{String.valueOf(teamId)});

        while (cursor.moveToNext()) {
            players.add(new Player(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_PLAYER_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_PLAYER_TEAM_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PLAYER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PLAYER_POSITION)),
                    "" // Sport type will be set from the team
            ));
        }
        cursor.close();
        db.close();
        return players;
    }

    public int updatePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PLAYER_NAME, player.getName());
        values.put(COL_PLAYER_POSITION, player.getPosition());
        int result = db.update(TABLE_PLAYERS, values, COL_PLAYER_ID + " = ?",
                new String[]{String.valueOf(player.getId())});
        db.close();
        return result;
    }

    public void deletePlayer(long playerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYERS, COL_PLAYER_ID + " = ?",
                new String[]{String.valueOf(playerId)});
        db.close();
    }

    // Get all teams
    public List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TEAMS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Team team = new Team(
                    cursor.getInt(cursor.getColumnIndex(COL_TEAM_ID)),
                    cursor.getString(cursor.getColumnIndex(COL_TEAM_NAME)),
                    cursor.getString(cursor.getColumnIndex(COL_TEAM_SPORT)),
                    cursor.getString(cursor.getColumnIndex(COL_TEAM_DESC))
                );
                
                // Get players for this team
                List<Player> players = getTeamPlayers(team.getId());
                team.setPlayers(players);
                
                teams.add(team);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return teams;
    }

    public Team getTeamById(int teamId) {
        Team team = null;
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Query to get team details
        Cursor teamCursor = db.query(
            TABLE_TEAMS,
            new String[]{"id", "name", "description", "sport_type"},
            "id = ?",
            new String[]{String.valueOf(teamId)},
            null, null, null
        );

        if (teamCursor != null && teamCursor.moveToFirst()) {
            team = new Team(
                teamCursor.getInt(teamCursor.getColumnIndexOrThrow("id")),
                teamCursor.getString(teamCursor.getColumnIndexOrThrow("name")),
                teamCursor.getString(teamCursor.getColumnIndexOrThrow("description")),
                teamCursor.getString(teamCursor.getColumnIndexOrThrow("sport_type"))
            );
            teamCursor.close();

            // Get players for this team
            Cursor playerCursor = db.query(
                TABLE_PLAYERS,
                new String[]{"id", "name", "position", "team_id"},
                "team_id = ?",
                new String[]{String.valueOf(teamId)},
                null, null, null
            );

            if (playerCursor != null) {
                while (playerCursor.moveToNext()) {
                    Player player = new Player(
                        playerCursor.getInt(playerCursor.getColumnIndexOrThrow("id")),
                        playerCursor.getInt(playerCursor.getColumnIndexOrThrow("team_id")),
                        playerCursor.getString(playerCursor.getColumnIndexOrThrow("name")),
                        playerCursor.getString(playerCursor.getColumnIndexOrThrow("position")),
                        "" // Sport type will be set from the team
                    );
                    team.addPlayer(player);
                }
                playerCursor.close();
            }
        }

        return team;
    }

    // Event methods
    public long addEvent(String title, String description, String location, String date, 
                        String time, String sportType, int maxParticipants) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        
        cv.put(COL_EVENT_TITLE, title);
        cv.put(COL_EVENT_DESC, description);
        cv.put(COL_EVENT_LOCATION, location);
        cv.put(COL_EVENT_DATE, date);
        cv.put(COL_EVENT_TIME, time);
        cv.put(COL_EVENT_SPORT_TYPE, sportType);
        cv.put(COL_EVENT_MAX_PARTICIPANTS, maxParticipants);
        cv.put(COL_EVENT_CURRENT_PARTICIPANTS, 0);
        
        long result = db.insert(TABLE_EVENTS, null, cv);
        db.close();
        return result;
    }

    public boolean updateEvent(long eventId, String title, String description, String location,
                             String date, String time, String sportType, int maxParticipants) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        
        cv.put(COL_EVENT_TITLE, title);
        cv.put(COL_EVENT_DESC, description);
        cv.put(COL_EVENT_LOCATION, location);
        cv.put(COL_EVENT_DATE, date);
        cv.put(COL_EVENT_TIME, time);
        cv.put(COL_EVENT_SPORT_TYPE, sportType);
        cv.put(COL_EVENT_MAX_PARTICIPANTS, maxParticipants);
        
        int result = db.update(TABLE_EVENTS, cv, COL_EVENT_ID + " = ?", 
                             new String[]{String.valueOf(eventId)});
        db.close();
        return result > 0;
    }

    public boolean deleteEvent(long eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EVENTS, COL_EVENT_ID + " = ?", 
                             new String[]{String.valueOf(eventId)});
        db.close();
        return result > 0;
    }

    public List<EventModel> getAllEvents() {
        List<EventModel> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_EVENTS, null, null, null, null, null, 
                               COL_EVENT_DATE + " ASC, " + COL_EVENT_TIME + " ASC");
        
        if (cursor.moveToFirst()) {
            do {
                EventModel event = new EventModel(
                    cursor.getLong(cursor.getColumnIndex(COL_EVENT_ID)),
                    cursor.getString(cursor.getColumnIndex(COL_EVENT_TITLE)),
                    cursor.getString(cursor.getColumnIndex(COL_EVENT_DESC)),
                    cursor.getString(cursor.getColumnIndex(COL_EVENT_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(COL_EVENT_DATE)),
                    cursor.getString(cursor.getColumnIndex(COL_EVENT_TIME)),
                    cursor.getString(cursor.getColumnIndex(COL_EVENT_SPORT_TYPE)),
                    cursor.getInt(cursor.getColumnIndex(COL_EVENT_MAX_PARTICIPANTS)),
                    cursor.getInt(cursor.getColumnIndex(COL_EVENT_CURRENT_PARTICIPANTS))
                );
                events.add(event);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return events;
    }

    public EventModel getEventById(long eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        EventModel event = null;
        
        Cursor cursor = db.query(TABLE_EVENTS, null, COL_EVENT_ID + " = ?",
                               new String[]{String.valueOf(eventId)}, null, null, null);
        
        if (cursor.moveToFirst()) {
            event = new EventModel(
                cursor.getLong(cursor.getColumnIndex(COL_EVENT_ID)),
                cursor.getString(cursor.getColumnIndex(COL_EVENT_TITLE)),
                cursor.getString(cursor.getColumnIndex(COL_EVENT_DESC)),
                cursor.getString(cursor.getColumnIndex(COL_EVENT_LOCATION)),
                cursor.getString(cursor.getColumnIndex(COL_EVENT_DATE)),
                cursor.getString(cursor.getColumnIndex(COL_EVENT_TIME)),
                cursor.getString(cursor.getColumnIndex(COL_EVENT_SPORT_TYPE)),
                cursor.getInt(cursor.getColumnIndex(COL_EVENT_MAX_PARTICIPANTS)),
                cursor.getInt(cursor.getColumnIndex(COL_EVENT_CURRENT_PARTICIPANTS))
            );
        }
        
        cursor.close();
        db.close();
        return event;
    }
}
