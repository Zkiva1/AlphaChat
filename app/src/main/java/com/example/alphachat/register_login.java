package com.example.alphachat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class register_login extends AppCompatActivity {

    Button login_btn, register_btn;
    TextView forgotPasswordTv;
    EditText login_email, login_password;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_login);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        forgotPasswordTv = findViewById(R.id.forgot_password_tv);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);

        forgotPasswordTv.setOnClickListener(v -> {
            Intent intent = new Intent(register_login.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login
                String email, password;
                email = login_email.getText().toString();
                password = login_password.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                login_btn.setVisibility(View.GONE);
                register_btn.setVisibility(View.GONE);

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.GONE);
                    login_btn.setVisibility(View.VISIBLE);
                    register_btn.setVisibility(View.VISIBLE);
                    Toast.makeText(register_login.this, "Email or Password empty", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    UserModel userModel = task2.getResult().toObject(UserModel.class);
                                    Intent intent;

                                    if (userModel == null || userModel.getUsername() == null || userModel.getUsername().isEmpty()) {
                                        // No username yet
                                        intent = new Intent(register_login.this, LoginUsernameActivity.class);
                                        intent.putExtra("email", login_email.getText().toString().trim());

                                    } else if (userModel.getOccupation() == null || userModel.getOccupation().isEmpty()) {
                                        // Username exists, but no occupation yet
                                        intent = new Intent(register_login.this, OccupationRegisterActivity.class);

                                        intent.putExtra("username", userModel.getUsername());
                                        intent.putExtra("email", login_email.getText().toString().trim());

                                    } else {
                                        // Both username and occupation exist
                                        intent = new Intent(register_login.this, MainActivity.class);
                                    }

                                    // Start the activity
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    login_btn.setVisibility(View.VISIBLE);
                                    register_btn.setVisibility(View.VISIBLE);
                                    Toast.makeText(register_login.this, task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            login_btn.setVisibility(View.VISIBLE);
                            register_btn.setVisibility(View.VISIBLE);
                            Toast.makeText(register_login.this, "Email or password incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });;

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //register
                Intent intent = new Intent(register_login.this, RegisterEmail.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }

        });
    }
}