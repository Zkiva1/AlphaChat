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

/**
 * Activity for displaying detailed information about a specific Mechina (academy).
 *
 * This activity acts as a container for academy-specific data, using a
 * {@link BottomNavigationView} to toggle between {@link StudantsFragment} and
 * {@link TeachersFragment}. It also provides access to the academy's official website.
 */
public class MechinaActivity extends AppCompatActivity {

    /** Button to navigate back. Binds to {@code mechina_back_btn}. */
    ImageButton backButton;
    /** Button to launch the academy's official website. Binds to {@code website_btn}. */
    ImageButton mechinaWebsiteBtn;
    /** Displays the name of the academy. Binds to {@code mechina_name}. */
    TextView mechinaName;
    /** The data model representing the academy being viewed. */
    Mechina mechinaModel;
    /** Bottom navigation for switching between students and teachers. Binds to {@code mechina_navigation}. */
    BottomNavigationView bottomNavigationView;

    /**
     * Called when the activity is first created.
     *
     * Initializes UI components, extracts academy details from the intent, and
     * sets up the fragment navigation flow.
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mechina);


        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mechinaModel = AndroidUtil.getMechinaModelFromIntent(getIntent());

        backButton = findViewById(R.id.mechina_back_btn);
        mechinaName = findViewById(R.id.mechina_name);
        mechinaWebsiteBtn = findViewById(R.id.website_btn);
        bottomNavigationView = findViewById(R.id.mechina_navigation);

        mechinaName.setText(mechinaModel.getName());

        mechinaWebsiteBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mechinaModel.getLink()));
            Intent chooser = Intent.createChooser(intent, "Choose your browser");
            startActivity(chooser);
        });

        backButton.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.menu_students) {
                selectedFragment = new StudantsFragment();
            } else if (item.getItemId() == R.id.menu_teachers) {
                selectedFragment = new TeachersFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        if (savedInstanceState == null) {
            loadFragment(new StudantsFragment());
        }
    }

    /**
     * Replaces the main frame with a new fragment and injects academy data.
     *
     * @param fragment The {@link Fragment} to be displayed.
     */
    private void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("mechina_model", mechinaModel);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout, fragment)
                .commit();
    }
}