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

import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity for the first step of user registration: email entry.
 *
 * This activity validates the user's email format and passes it to the next
 * registration step.
 *
 * Firebase Authentication.
 */
public class RegisterEmail extends AppCompatActivity {

    /** Input field for the user's email address. Binds to {@code register_email}. */
    EditText emailInput;
    /** Button to proceed to the next registration step. Binds to {@code next_to_password}. */
    Button nextBtn;
    /** Progress bar for async operations (currently unused). Binds to {@code register_email_progress_bar}. */
    ProgressBar progressBar;

    /** Reference to Firebase Authentication service. */
    FirebaseAuth mAuth;


    /**
     * Called when the activity is first created.
     *
     * Initializes UI components and sets up the click listener for email validation
     * and navigation.
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_email);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        mAuth = FirebaseAuth.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInput = findViewById(R.id.register_email);
        nextBtn = findViewById(R.id.next_to_password);
        progressBar = findViewById(R.id.register_email_progress_bar);

        progressBar.setVisibility(View.GONE);

        nextBtn.setOnClickListener((view -> {

            String email = emailInput.getText().toString();

            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Valid email
                Intent intent = new Intent(RegisterEmail.this, RegisterPassword.class);
                intent.putExtra("email", emailInput.getText().toString().trim());
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else {
                // Invalid email
                emailInput.setError("Please enter a valid email");
            }

        }));
    }
}