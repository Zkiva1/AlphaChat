package com.example.alphachat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        if(FirebaseUtil.isLoggedIn() && getIntent().getExtras()!=null) {
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            UserModel userModel = task.getResult().toObject(UserModel.class);

                            Intent mainIntent = new Intent(this, MainActivity.class);
                            mainIntent.setFlags(mainIntent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent intent = new Intent(this, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent,userModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                    });
        }else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (FirebaseUtil.isLoggedIn()) {
                        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task2 -> {
                            if(task2.isSuccessful()) {
                                Intent intent;
                                UserModel userModel = task2.getResult().toObject(UserModel.class);

                                if (userModel == null || userModel.getUsername() == null || userModel.getUsername().isEmpty()) {
                                    intent = new Intent(SplashActivity.this, LoginUsernameActivity.class);
                                    String email = (userModel != null) ? userModel.getEmail() : "";
                                    if (TextUtils.isEmpty(email) && FirebaseAuth.getInstance().getCurrentUser() != null) {
                                        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    }
                                    intent.putExtra("email", email);

                                } else if (userModel.getOccupation() == null || userModel.getOccupation().isEmpty()) {
                                    intent = new Intent(SplashActivity.this, OccupationRegisterActivity.class);
                                    intent.putExtra("username", userModel.getUsername());
                                    intent.putExtra("email", userModel.getEmail());

                                } else {
                                    intent = new Intent(SplashActivity.this, MainActivity.class);
                                }

                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();

                            } else {
                                String errorMsg = (task2.getException() != null) ? task2.getException().getMessage() : "Error fetching data";
                                Toast.makeText(SplashActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

                                // Fallback to login screen if the data fetch fails completely
                                startActivity(new Intent(SplashActivity.this, register_login.class));
                                finish();
                            }
                        });

                    } else {
                        startActivity(new Intent(SplashActivity.this, register_login.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }
            }, 1000);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}