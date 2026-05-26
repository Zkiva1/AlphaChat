package com.example.alphachat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.alphachat.model.Mechina;
import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.FirebaseUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class OccupationRegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView occupationPicker;
    private TextInputLayout mechiaPickerLayout;
    private AutoCompleteTextView mechiaPicker;
    private ProgressBar progressBar;
    private Button registerOccupationBtn;

    private String email;
    private String username;
    private List<Mechina> fullMechinaList = new ArrayList<>();
    private List<String> allMechinotNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_occupation_register);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        occupationPicker = findViewById(R.id.register_occupation_spinner);
        mechiaPickerLayout = findViewById(R.id.mechia_picker_layout);
        mechiaPicker = findViewById(R.id.mechia_picker);
        progressBar = findViewById(R.id.register_occupation_progress_bar);
        registerOccupationBtn = findViewById(R.id.register_occupation_btn);

        // Get Intent Extras
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");

        // Load Mechina Data
        fullMechinaList = JsonReader.convertJsonToObject(this);
        for (Mechina mechina : fullMechinaList) {
            allMechinotNames.add(mechina.getName());
        }

        // Set up Mechina Adapter
        ArrayAdapter<String> mechiaAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                allMechinotNames
        );
        mechiaPicker.setAdapter(mechiaAdapter);

        // Set up Occupation Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.occupation_array,
                android.R.layout.simple_list_item_1
        );
        occupationPicker.setAdapter(adapter);

        // Listeners for Validation & UI Toggling
        setupListeners();

        // Register Button Click
        registerOccupationBtn.setOnClickListener(view -> setOccupation());
    }

    private void setupListeners() {
        mechiaPicker.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String userInput = mechiaPicker.getText().toString();
                if (!userInput.isEmpty() && !allMechinotNames.contains(userInput)) {
                    mechiaPickerLayout.setError("Please select a valid Mechina from the list");
                    mechiaPicker.setText(""); // Erase invalid typing
                } else {
                    mechiaPickerLayout.setError(null);
                }
            }
        });

        mechiaPicker.setOnItemClickListener((parent, view, position, id) -> {
            mechiaPickerLayout.setError(null);
        });

        occupationPicker.setOnItemClickListener((parent, view, position, id) -> {
            String selectedValue = (String) parent.getItemAtPosition(position);

            if (selectedValue.equals("מכיניסט") || selectedValue.equals("ר\"מ או מדריך")) {
                mechiaPicker.setText("");
                mechiaPickerLayout.setVisibility(View.VISIBLE);
            } else {
                mechiaPickerLayout.setVisibility(View.GONE);
                mechiaPicker.setText("תיכון");
                mechiaPickerLayout.setError(null); // Clear errors when hidden
            }
        });
    }

    private void setOccupation() {
        String occupation = occupationPicker.getText().toString().trim();
        String mechina = mechiaPicker.getText().toString().trim();

        // 1. Validate Occupation
        if (occupation.isEmpty()) {
            occupationPicker.setError("Please enter occupation");
            return; // Stop execution
        }

        // 2. Validate Mechina Text Presence
        if (mechina.isEmpty()) {
            mechiaPickerLayout.setError("Please enter mechina");
            return; // Stop execution
        }

        // 3. Validate Mechina against official list (Only if the layout is currently visible)
        if (mechiaPickerLayout.getVisibility() == View.VISIBLE && !allMechinotNames.contains(mechina)) {
            mechiaPickerLayout.setError("Please select a valid Mechina from the list");
            mechiaPicker.setText("");
            return; // Stop execution
        }

        // 4. Data is completely valid - proceed with saving
        mechiaPickerLayout.setError(null);
        setInProgress(true);

        UserModel userModel = new UserModel(
                email,
                username,
                Timestamp.now(),
                FirebaseUtil.currentUserId(),
                null,
                occupation,
                mechina
        );

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            setInProgress(false); // Stop loading animation whether success or fail

            if (task.isSuccessful()) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else {
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            registerOccupationBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            registerOccupationBtn.setVisibility(View.VISIBLE);
        }
    }
}