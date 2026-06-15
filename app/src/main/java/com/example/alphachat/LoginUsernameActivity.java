package com.example.alphachat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Activity for the third step of user registration: username entry.
 *
 * This activity handles the collection and validation of the user's chosen display
 * name during the sign-up process.
 */
public class LoginUsernameActivity extends AppCompatActivity {

    /** Input field for the user's chosen username. Binds to {@code register_username}. */
    EditText registerUsername;
    /** Button to proceed to the next registration step. Binds to {@code register_username_btn}. */
    Button registerUsernameBtn;
    /** Progress bar for UI feedback. Binds to {@code register_username_progress_bar}. */
    ProgressBar progressBar;
    /** The user's email address passed from previous steps. */
    String email;

    /**
     * Called when the activity is first created.
     *
     * Initializes UI components and sets up validation logic for the username input.
     * Redirects to the occupation selection screen upon valid input.
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_username);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerUsername = findViewById(R.id.register_username);
        registerUsernameBtn = findViewById(R.id.register_username_btn);
        progressBar = findViewById(R.id.register_username_progress_bar);
        progressBar.setVisibility(View.GONE);
        email = getIntent().getStringExtra("email");

        registerUsernameBtn.setOnClickListener((view -> {
            String username = registerUsername.getText().toString().trim();
            if (username.isEmpty() || username.length() < 3) {
                registerUsernameBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                registerUsername.setError("Username should be at least 3 characters long");
            } else {
                Intent intent = new Intent(LoginUsernameActivity.this, OccupationRegisterActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("username", registerUsername.getText().toString().trim());
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        }));

    }

}