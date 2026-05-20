package com.example.alphachat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.example.alphachat.model.Mechina;
import com.example.alphachat.utils.AndroidUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MechinaActivity extends AppCompatActivity {

    ImageButton backButton, mechinaWebsiteBtn;
    TextView mechinaName;
    Mechina mechinaModel;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mechina);

        // Setup light status bar
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);

        // Handle window insets to prevent UI from hiding behind system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Retrieve the Mechina object passed from the adapter
        mechinaModel = AndroidUtil.getMechinaModelFromIntent(getIntent());

        // 2. Bind views to their XML IDs
        backButton = findViewById(R.id.mechina_back_btn);
        mechinaName = findViewById(R.id.mechina_name);
        mechinaWebsiteBtn = findViewById(R.id.website_btn);
        bottomNavigationView = findViewById(R.id.mechina_navigation);

        // 3. Populate UI with data
        mechinaName.setText(mechinaModel.getName());

        // 4. Set Click Listeners
        mechinaWebsiteBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mechinaModel.getLink()));
            Intent chooser = Intent.createChooser(intent, "Choose your browser");
            startActivity(chooser);
        });

        backButton.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // 5. Handle Bottom Navigation Clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Note: Make sure these IDs match what is inside your res/menu/mechinot_navigation_menu.xml
            if (item.getItemId() == R.id.menu_students) {
                selectedFragment = new StudantsFragment();
            } else if (item.getItemId() == R.id.menu_teachers) {
                selectedFragment = new TeachersFragment();
            }

            // Swap the fragment safely
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        // 6. Load the default fragment when the activity first opens
        if (savedInstanceState == null) {
            loadFragment(new StudantsFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        // Package the mechina data to send to the fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("mechina_model", mechinaModel);
        fragment.setArguments(bundle);

        // Replace whatever is currently in the main_frame_layout with the new fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout, fragment)
                .commit();
    }
}