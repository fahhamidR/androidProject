package com.example.fahim.controllers;

import android.content.Context;
import com.example.fahim.models.EventModel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.util.Log;

public class EventController extends BaseController {
    private List<EventModel> events;

    public EventController(Context context, boolean isAdmin) {
        super(context, isAdmin);
        this.events = new ArrayList<>();
    }

    // Admin operations
    public long addEvent(String title, String description, String location,
                        String date, String time, String sportType, int maxParticipants) {
        checkAdminAccess();
        if (title == null || location == null || date == null || time == null ||
            sportType == null || title.trim().isEmpty() || location.trim().isEmpty() ||
            date.trim().isEmpty() || time.trim().isEmpty() || sportType.trim().isEmpty()) {
            return -1;
        }
        
        if (maxParticipants <= 0) {
            return -1;
        }

        return dbHelper.addEvent(title.trim(), description.trim(), location.trim(),
                               date.trim(), time.trim(), sportType.trim(), maxParticipants);
    }

    public boolean updateEvent(long eventId, String title, String description, String location,
                             String date, String time, String sportType, int maxParticipants) {
        checkAdminAccess();
        if (eventId <= 0 || title == null || location == null || date == null ||
            time == null || sportType == null || title.trim().isEmpty() ||
            location.trim().isEmpty() || date.trim().isEmpty() || time.trim().isEmpty() ||
            sportType.trim().isEmpty()) {
            return false;
        }

        if (maxParticipants <= 0) {
            return false;
        }

        return dbHelper.updateEvent(eventId, title.trim(), description.trim(), location.trim(),
                                  date.trim(), time.trim(), sportType.trim(), maxParticipants);
    }

    public boolean deleteEvent(long eventId) {
        checkAdminAccess();
        if (eventId <= 0) {
            return false;
        }
        return dbHelper.deleteEvent(eventId);
    }

    public boolean registerParticipant(long eventId) {
        checkAdminAccess();
        if (eventId <= 0) {
            return false;
        }

        // Find event and register participant
        for (EventModel event : events) {
            if (event.getId() == eventId) {
                if (event.isFull()) {
                    return false;
                }
                // In a real app, this would update the database
                // For now, we'll just increment the counter
                return true;
            }
        }
        return false;
    }

    // User operations (read-only)
    public List<EventModel> getAllEvents() {
        try {
            checkUserAccess();
            return dbHelper.getAllEvents();
        } catch (Exception e) {
            Log.e("EventController", "Error getting events: " + e.getMessage());
            throw e;
        }
    }

    public List<EventModel> getEventsBySport(String sportType) {
        checkUserAccess();
        if (sportType == null || sportType.trim().isEmpty()) {
            return null;
        }

        List<EventModel> allEvents = getAllEvents();
        List<EventModel> filteredEvents = new ArrayList<>();
        
        for (EventModel event : allEvents) {
            if (sportType.trim().equalsIgnoreCase(event.getSportType())) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    public EventModel getEventById(long eventId) {
        checkUserAccess();
        if (eventId <= 0) {
            return null;
        }
        return dbHelper.getEventById(eventId);
    }

    // Helper method to load sample events (for demonstration)
    public void loadSampleEvents() {
        checkUserAccess();
        events.clear();
        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        // Sample event 1
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        events.add(new EventModel(
            1,
            "Football Tournament",
            "Annual inter-college football tournament",
            "Main Sports Complex",
            dateFormat.format(calendar.getTime()),
            timeFormat.format(calendar.getTime()),
            "Football",
            20,
            15
        ));

        // Sample event 2
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        events.add(new EventModel(
            2,
            "Cricket Match",
            "Friendly match between departments",
            "Cricket Ground",
            dateFormat.format(calendar.getTime()),
            timeFormat.format(calendar.getTime()),
            "Cricket",
            22,
            22
        ));
    }
} 