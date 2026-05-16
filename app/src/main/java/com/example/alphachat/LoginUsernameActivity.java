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
import androidx.core.view.WindowInsetsCompat;

import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText registerUsername;
    Button registerFinishBtn;
    ProgressBar progressBar;
    String email;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_username);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerUsername = findViewById(R.id.register_username);
        registerFinishBtn = findViewById(R.id.register_finish_btn);
        progressBar = findViewById(R.id.register_username_progress_bar);
        progressBar.setVisibility(View.GONE);
        email = getIntent().getStringExtra("email");
        getUsername();

        registerFinishBtn.setOnClickListener((view -> { setUsername(); }));

    }

    void setUsername() {

        progressBar.setVisibility(View.VISIBLE);
        registerFinishBtn.setVisibility(View.GONE);

        String username = registerUsername.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            registerFinishBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            registerUsername.setError("Username should be at least 3 characters long");
            return;
        } else {
            userModel = new UserModel(email, username, Timestamp.now());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginUsernameActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    registerFinishBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    void getUsername() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel!=null) {
                        registerUsername.setText(userModel.getUsername());
                    }
                }
            }
        });

    }

}