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
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterEmail extends AppCompatActivity {

    EditText emailInput;
    Button nextBtn;
    ProgressBar progressBar;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_email);
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
                intent.putExtra("email", emailInput.getText().toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else {
                // Invalid email
                emailInput.setError("Please enter a valid email");
            }

        }));
    }
}