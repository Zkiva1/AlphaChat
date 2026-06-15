package com.example.alphachat;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphachat.adapter.SearchUserRecyclerAdapter;
import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * Activity that allows users to search for other participants by username.
 *
 * This activity provides a search interface that queries the Firestore {@code users}
 * collection in real-time as the user types. It displays results in a RecyclerView
 * using the {@link SearchUserRecyclerAdapter}.
 *
 * Cloud Firestore {@code users} collection.
 */
public class SearchUserActivity extends AppCompatActivity {

    /** The search bar for entering usernames. Binds to {@code search_username_input}. */
    private SearchView searchInput;
    /** Button to navigate back to the previous screen. Binds to {@code back_btn}. */
    private ImageButton backButton;
    /** RecyclerView to display search results. Binds to {@code search_user_recycler_view}. */
    private RecyclerView recyclerView;

    /** Adapter for binding user search results to the RecyclerView. */
    SearchUserRecyclerAdapter adapter;

    /**
     * Called when the activity is first created.
     *
     * Initializes UI components, sets up Edge-to-Edge display, and configures the
     * {@link SearchView} with query listeners to trigger real-time search.
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchInput = findViewById(R.id.search_username_input);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        backButton.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });



        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setupSearchRecyclerView(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    if (adapter != null) {
                        adapter.stopListening();
                    }
                } else {
                    setupSearchRecyclerView(newText);
                }
                return true;
            }
        });
    }

    /**
     * Configures the RecyclerView with a Firestore query based on the search term.
     *
     * Uses a prefix-matching strategy to find users whose usernames start with
     * the provided string.
     *
     * @param searchTerm The string to search for in usernames.
     */
    void setupSearchRecyclerView(String searchTerm) {
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username", searchTerm + '\uf8ff');

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        if (adapter == null) {
            adapter = new SearchUserRecyclerAdapter(options, SearchUserActivity.this);
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

            recyclerView.setLayoutManager(new AndroidUtil.SafeLinearLayoutManager(this));

            recyclerView.setAdapter(adapter);
            adapter.startListening();
        } else {
            adapter.updateOptions(options);
            adapter.startListening();
        }
    }

    /**
     * Called when the activity is visible to the user.
     *
     * Resumes the Firestore adapter listening if it exists.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    /**
     * Called when the activity is no longer visible.
     *
     * Stops the Firestore adapter from listening to conserve resources.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }


}
