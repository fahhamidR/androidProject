package com.example.fahim.controllers;

import android.content.Context;
import com.example.fahim.DatabaseHelper;

public abstract class BaseController {
    protected DatabaseHelper dbHelper;
    protected Context context;
    protected boolean isAdmin;

    public BaseController(Context context, boolean isAdmin) {
        this.context = context;
        this.isAdmin = isAdmin;
        this.dbHelper = new DatabaseHelper(context);
    }

    protected boolean checkAdminAccess() {
        if (!isAdmin) {
            throw new SecurityException("Admin access required for this operation");
        }
        return true;
    }

    protected boolean checkUserAccess() {
        return true; // All authenticated users have basic access
    }
} 