package com.example.alphachat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.alphachat.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * The main container activity for the application.
 *
 * This activity manages the primary navigation flow using a {@link BottomNavigationView}
 * to switch between {@link ChatFragment}, {@link MechinotFragment}, and {@link ProfileFragment}.
 * It also handles FCM token registration and notification permissions.
 *
 * Firebase Cloud Messaging, Cloud Firestore {@code users} collection.
 */
public class MainActivity extends AppCompatActivity {

    /** The bottom navigation bar for switching fragments. Binds to {@code bottom_navigation}. */
    BottomNavigationView bottomNavigationView;
    /** Button to launch the user search activity. Binds to {@code main_search_btn}. */
    ImageButton searchButton;
    /** Persistent instance of the chat fragment. */
    ChatFragment chatFragment;
    /** Persistent instance of the academies browser fragment. */
    MechinotFragment mapFragment;
    /** Persistent instance of the user profile fragment. */
    ProfileFragment profileFragment;




    /**
     * Called when the activity is first created.
     *
     * Sets up Edge-to-Edge UI, initializes fragments, configures navigation listeners,
     * and initiates notification permission checks.
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        mapFragment = new MechinotFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);

        searchButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
            overridePendingTransition(0, 0);
        });

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId()==R.id.menu_chat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
            }
            if (menuItem.getItemId()==R.id.menu_map) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, mapFragment).commit();
            }
            if (menuItem.getItemId()==R.id.menu_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        checkNotificationPermissionAndGetToken();

    }

    /**
     * Checks for POST_NOTIFICATIONS permission on Android 13+ and retrieves FCM token.
     *
     * If permission is granted or the OS version is lower than Tiramisu, it proceeds
     * to fetch the FCM token. Otherwise, it requests permission from the user.
     */
    void checkNotificationPermissionAndGetToken() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Permission already granted, safe to get token
                getFCMToken();
            } else {
                // Ask the user for permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        } else {
            // Android 12 or lower gets permission automatically at install
            getFCMToken();
        }
    }

    /**
     * Retrieves the Firebase Cloud Messaging token and updates the user's Firestore document.
     *
     * @implNote This method initiates an asynchronous Firestore operation; the UI is updated
     * via the supplied callback on the main thread.
     */
    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken", token);
            }
        });
    }

}