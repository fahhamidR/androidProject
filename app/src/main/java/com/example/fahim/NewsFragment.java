package com.example.fahim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.example.fahim.adapters.NewsAdapter;
import com.example.fahim.models.NewsModel;
import com.example.fahim.controllers.NewsController;

public class NewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<NewsModel> newsList;
    private NewsController newsController;
    private FloatingActionButton fabAddNews;
    private boolean isAdmin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAdmin = ((MainActivity) requireActivity()).isAdmin();
        newsController = new NewsController(requireContext(), isAdmin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        // Initialize views
        fabAddNews = view.findViewById(R.id.fabAddNews);
        recyclerView = view.findViewById(R.id.recyclerViewNews);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Show/hide FAB based on admin status
        if (isAdmin) {
            fabAddNews.setVisibility(View.VISIBLE);
            fabAddNews.setOnClickListener(v -> showAddNewsDialog());
        } else {
            fabAddNews.setVisibility(View.GONE);
        }

        loadNews();
        return view;
    }

    private void loadNews() {
        try {
            newsList = newsController.getAllNews();
            adapter = new NewsAdapter(getContext(), newsList, isAdmin);
            recyclerView.setAdapter(adapter);
        } catch (SecurityException e) {
            Toast.makeText(requireContext(), "Access denied", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error loading news", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddNewsDialog() {
        if (!isAdmin) {
            Toast.makeText(requireContext(), "Admin access required", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add News");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_news, null);
        EditText etTitle = dialogView.findViewById(R.id.etNewsTitle);
        EditText etDesc = dialogView.findViewById(R.id.etNewsDescription);
        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String description = etDesc.getText().toString().trim();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            try {
                if (newsController.addNews(title, description, currentDate)) {
                    loadNews();
                    Toast.makeText(requireContext(), "News added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to add news", Toast.LENGTH_SHORT).show();
                }
            } catch (SecurityException e) {
                Toast.makeText(requireContext(), "Admin access required", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error adding news", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerView != null) {
            loadNews(); // Refresh news when returning to this fragment
        }
    }
}
