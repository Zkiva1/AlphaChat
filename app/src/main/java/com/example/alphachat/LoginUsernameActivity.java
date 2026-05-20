package com.example.alphachat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText registerUsername;
    Button registerUsernameBtn;
    ProgressBar progressBar;
    String email;

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