package com.example.alphachat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton, backButton;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchInput = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_recycler_view);

        searchInput.requestFocus();

        backButton.setOnClickListener(
                view -> getOnBackPressedDispatcher().onBackPressed()
        );

        searchButton.setOnClickListener(view -> {
            String searchTerm = searchInput.getText().toString();
            if (searchTerm.isEmpty()) {
                searchInput.setError("Search Empty");
                return;
            }
            //setupSearchRecyclerView
        });
    }

    void setupSearchRecyclerView(String searchTerm) {

    }

}