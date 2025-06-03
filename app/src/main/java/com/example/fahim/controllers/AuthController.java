package com.example.fahim.controllers;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthController extends BaseController {
    private static final String PREF_NAME = "login_pref";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_ROLE = "role";

    public AuthController(Context context) {
        super(context, false); // Initial state doesn't matter for auth
    }

    public boolean registerUser(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        return dbHelper.registerUser(email.trim().toLowerCase(), password);
    }

    public boolean loginUser(String email, String password, boolean isAdminLogin) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        String trimmedEmail = email.trim().toLowerCase();
        if (dbHelper.checkUser(trimmedEmail, password)) {
            boolean isAdmin = dbHelper.isAdmin(trimmedEmail);
            
            // Validate admin login attempt
            if (isAdminLogin && !isAdmin) {
                return false;
            }

            // Save login state
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_EMAIL, trimmedEmail);
            editor.putString(PREF_ROLE, isAdmin ? "admin" : "user");
            editor.apply();
            
            return true;
        }
        return false;
    }

    public void logout() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public boolean isUserLoggedIn() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.contains(PREF_EMAIL);
    }

    public boolean isCurrentUserAdmin() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return "admin".equals(prefs.getString(PREF_ROLE, "user"));
    }

    public String getCurrentUserEmail() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_EMAIL, "");
    }
} 