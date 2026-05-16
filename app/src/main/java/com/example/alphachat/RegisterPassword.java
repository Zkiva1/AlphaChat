package com.example.alphachat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterPassword extends AppCompatActivity {

    String email;
    EditText passwordInput, confirmPasswordInput;
    Button nextBtn;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_password);
        mAuth = FirebaseAuth.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = getIntent().getExtras().getString("email");
        passwordInput = findViewById(R.id.register_password);
        confirmPasswordInput = findViewById(R.id.confirm_register_password);
        nextBtn = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.register_password_progress_bar);
        progressBar.setVisibility(View.GONE);

        Log.d("EMAIL_TEST", "Email: " + email);

        nextBtn.setOnClickListener(view -> {

            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.INVISIBLE);

            String password = passwordInput.getText().toString();
            String password2 = confirmPasswordInput.getText().toString();

            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {

                progressBar.setVisibility(View.INVISIBLE);
                nextBtn.setVisibility(View.VISIBLE);

                Toast.makeText(
                        RegisterPassword.this,
                        "Password is empty",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!password.equals(password2)) {

                progressBar.setVisibility(View.INVISIBLE);
                nextBtn.setVisibility(View.VISIBLE);

                Toast.makeText(
                        RegisterPassword.this,
                        "Passwords are not the same",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (password.length() < 6) {

                progressBar.setVisibility(View.INVISIBLE);
                nextBtn.setVisibility(View.VISIBLE);

                Toast.makeText(
                        RegisterPassword.this,
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        progressBar.setVisibility(View.INVISIBLE);
                        nextBtn.setVisibility(View.VISIBLE);

                        if (task.isSuccessful()) {

                            String userId =
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getUid();

                            Toast.makeText(
                                    RegisterPassword.this,
                                    "Account created",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    RegisterPassword.this,
                                    LoginUsernameActivity.class
                            );

                            intent.putExtra("email", email);
                            startActivity(intent);
                            overridePendingTransition(0, 0);

                        } else {

                            Toast.makeText(
                                    RegisterPassword.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    RegisterPassword.this,
                                    RegisterEmail.class
                            );

                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    });
        });
    }
}