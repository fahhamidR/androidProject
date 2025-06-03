package com.example.fahim.controllers;

import android.content.Context;
import com.example.fahim.models.NewsModel;
import java.util.List;

public class NewsController extends BaseController {
    public NewsController(Context context, boolean isAdmin) {
        super(context, isAdmin);
    }

    // Admin operations
    public boolean addNews(String title, String description, String date) {
        checkAdminAccess();
        if (title == null || description == null || date == null ||
            title.trim().isEmpty() || description.trim().isEmpty() || date.trim().isEmpty()) {
            return false;
        }
        
        try {
            dbHelper.addNews(title.trim(), description.trim(), date.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteNews(int newsId) {
        checkAdminAccess();
        if (newsId <= 0) {
            return false;
        }
        return dbHelper.deleteNews(newsId);
    }

    // User operations (read-only)
    public List<NewsModel> getAllNews() {
        checkUserAccess();
        return dbHelper.getAllNews();
    }

    public NewsModel getNewsById(int newsId) {
        checkUserAccess();
        if (newsId <= 0) {
            return null;
        }
        
        List<NewsModel> allNews = dbHelper.getAllNews();
        for (NewsModel news : allNews) {
            if (news.getId() == newsId) {
                return news;
            }
        }
        return null;
    }
} 