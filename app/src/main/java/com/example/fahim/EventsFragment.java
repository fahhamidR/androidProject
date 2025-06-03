package com.example.fahim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.fahim.adapters.EventAdapter;
import com.example.fahim.models.EventModel;
import com.example.fahim.controllers.EventController;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import java.lang.Override;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.List;
import java.util.Calendar;
import java.util.ArrayList;
import android.util.Log;
import java.util.Arrays;
import android.view.Window;
import android.util.DisplayMetrics;
import android.widget.AutoCompleteTextView;

public class EventsFragment extends Fragment implements EventAdapter.OnEventClickListener {
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private EventController eventController;
    private FloatingActionButton fabAddEvent;
    private List<EventModel> events;
    private boolean isAdmin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAdmin = ((MainActivity) requireActivity()).isAdmin();
        eventController = new EventController(requireContext(), isAdmin);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        fabAddEvent = view.findViewById(R.id.fabAddEvent);

        // Initialize events list
        events = new ArrayList<>();

        // Setup RecyclerView
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(events, this, isAdmin);
        eventsRecyclerView.setAdapter(eventAdapter);

        // Show/hide FAB based on admin status
        if (isAdmin) {
            fabAddEvent.setVisibility(View.VISIBLE);
            fabAddEvent.setOnClickListener(v -> showAddEventDialog());
        } else {
            fabAddEvent.setVisibility(View.GONE);
        }

        loadEvents();
    }

    private void loadEvents() {
        try {
            List<EventModel> loadedEvents = eventController.getAllEvents();
            if (loadedEvents != null) {
                events.clear();
                events.addAll(loadedEvents);
                eventAdapter.updateEvents(events);
            } else {
                Toast.makeText(requireContext(), "No events found", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Log.e("EventsFragment", "Access denied: " + e.getMessage());
            Toast.makeText(requireContext(), "Access denied", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("EventsFragment", "Error loading events: " + e.getMessage());
            Toast.makeText(requireContext(), "Error loading events: " + e.getMessage(), 
                         Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddEventDialog() {
        if (!isAdmin) {
            Toast.makeText(requireContext(), "Admin access required", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Event");

        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_event, null);
        builder.setView(dialogView);

        // Initialize views
        EditText etTitle = dialogView.findViewById(R.id.etEventTitle);
        EditText etDescription = dialogView.findViewById(R.id.etEventDescription);
        EditText etLocation = dialogView.findViewById(R.id.etEventLocation);
        EditText etDate = dialogView.findViewById(R.id.etEventDate);
        EditText etTime = dialogView.findViewById(R.id.etEventTime);

        // Setup date picker
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String formattedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                        etDate.setText(formattedDate);
                    }, Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Setup time picker
        etTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute) -> {
                        String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                        etTime.setText(formattedTime);
                    }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Get values from views
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            // Validate inputs
            if (title.isEmpty() || location.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Add event to database with a default max participants value
                long result = eventController.addEvent(title, description, location, date, time,
                                             "General", 100); // Default sport type and max participants
                
                if (result != -1) {
                    Toast.makeText(requireContext(), "Event added successfully", 
                                 Toast.LENGTH_SHORT).show();
                    loadEvents(); // Refresh the events list
                } else {
                    Toast.makeText(requireContext(), "Failed to add event", 
                                 Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error adding event: " + e.getMessage(), 
                             Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set dialog width to 90% of screen width
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = (int) (displayMetrics.widthPixels * 0.9);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onEventClick(EventModel event) {
        // Show event details
        Toast.makeText(requireContext(), 
            "Event: " + event.getTitle() + "\n" +
            "Date: " + event.getEventDate() + "\n" +
            "Location: " + event.getLocation(),
            Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEditClick(EventModel event) {
        if (!isAdmin) {
            Toast.makeText(requireContext(), "Admin access required", Toast.LENGTH_SHORT).show();
            return;
        }
        showEditEventDialog(event);
    }

    @Override
    public void onDeleteClick(EventModel event) {
        if (!isAdmin) {
            Toast.makeText(requireContext(), "Admin access required", Toast.LENGTH_SHORT).show();
            return;
        }
        showDeleteConfirmationDialog(event);
    }

    private void showDeleteConfirmationDialog(EventModel event) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Delete", (dialog, which) -> {
                try {
                    if (eventController.deleteEvent(event.getId())) {
                        Toast.makeText(requireContext(), "Event deleted successfully", 
                                     Toast.LENGTH_SHORT).show();
                        loadEvents(); // Refresh the events list
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete event", 
                                     Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error deleting event: " + e.getMessage(), 
                                 Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showEditEventDialog(EventModel event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Event");

        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_event, null);
        builder.setView(dialogView);

        // Initialize views
        EditText etTitle = dialogView.findViewById(R.id.etEventTitle);
        EditText etDescription = dialogView.findViewById(R.id.etEventDescription);
        EditText etLocation = dialogView.findViewById(R.id.etEventLocation);
        EditText etDate = dialogView.findViewById(R.id.etEventDate);
        EditText etTime = dialogView.findViewById(R.id.etEventTime);

        // Set current values
        etTitle.setText(event.getTitle());
        etDescription.setText(event.getDescription());
        etLocation.setText(event.getLocation());
        etDate.setText(event.getEventDate());
        etTime.setText(event.getEventTime());

        // Setup date picker
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String formattedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                        etDate.setText(formattedDate);
                    }, Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Setup time picker
        etTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute) -> {
                        String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                        etTime.setText(formattedTime);
                    }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        builder.setPositiveButton("Update", (dialog, which) -> {
            // Get values from views
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            // Validate inputs
            if (title.isEmpty() || location.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", 
                             Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Update event in database
                boolean result = eventController.updateEvent(event.getId(), title, description, 
                    location, date, time, event.getSportType(), event.getMaxParticipants());
                
                if (result) {
                    Toast.makeText(requireContext(), "Event updated successfully", 
                                 Toast.LENGTH_SHORT).show();
                    loadEvents(); // Refresh the events list
                } else {
                    Toast.makeText(requireContext(), "Failed to update event", 
                                 Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error updating event: " + e.getMessage(), 
                             Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set dialog width to 90% of screen width
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = (int) (displayMetrics.widthPixels * 0.9);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (eventsRecyclerView != null) {
            loadEvents(); // Refresh events when returning to this fragment
        }
    }
}
