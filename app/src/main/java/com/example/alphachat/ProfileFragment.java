package com.example.alphachat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageView;
import com.example.alphachat.model.Mechina;
import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput, emailInput;

    TextInputLayout mechinaLayout, occupationLayout;
    AutoCompleteTextView occupationSpinner, mechinaSpinner;

    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    LinearLayout cropContainerLayout;
    CropImageView cropImageView;
    Button btnCancelCrop, btnConfirmCrop;
    Uri croppedImageUri;

    UserModel currentUserModel;

    private List<Mechina> fullMechinaList = new ArrayList<>();
    private List<String> allMechinotNames = new ArrayList<>();

    private ActivityResultLauncher<String> pickImageLauncher;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        cropContainerLayout.setVisibility(View.VISIBLE);
                        toggleMainActivityUi(View.GONE);
                        cropImageView.setImageUriAsync(uri);
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        emailInput = view.findViewById(R.id.profile_email);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        // Initialize New Occupation & Mechina Views
        occupationLayout = view.findViewById(R.id.profile_occupation_layout);
        occupationSpinner = view.findViewById(R.id.profile_occupation_spinner);
        mechinaLayout = view.findViewById(R.id.profile_mechina_layout);
        mechinaSpinner = view.findViewById(R.id.profile_mechina_spinner);

        cropContainerLayout = view.findViewById(R.id.crop_container_layout);
        cropImageView = view.findViewById(R.id.crop_image_view);
        btnCancelCrop = view.findViewById(R.id.btn_cancel_crop);
        btnConfirmCrop = view.findViewById(R.id.btn_confirm_crop);

        setupOccupationAndMechina();
        getUserData();

        profilePic.setOnClickListener(v -> {
            if (pickImageLauncher != null) {
                pickImageLauncher.launch("image/*");
            }
        });

        btnCancelCrop.setOnClickListener(v -> {
            cropContainerLayout.setVisibility(View.GONE);
            toggleMainActivityUi(View.VISIBLE);
            cropImageView.clearImage();
        });

        btnConfirmCrop.setOnClickListener(v -> {
            if (getContext() == null || !isAdded()) return;

            try {
                cropImageView.croppedImageAsync(
                        Bitmap.CompressFormat.JPEG,
                        85,
                        500,
                        500,
                        CropImageView.RequestSizeOptions.RESIZE_INSIDE,
                        null
                );
            } catch (Exception e) {
                e.printStackTrace();
                AndroidUtil.showToast(getContext(), "Error starting crop task: " + e.getMessage());
            }
        });

        cropImageView.setOnCropImageCompleteListener((cropView, result) -> {
            if (result.isSuccessful()) {
                croppedImageUri = result.getUriContent();

                if (croppedImageUri != null) {
                    Glide.with(this)
                            .load(croppedImageUri)
                            .circleCrop()
                            .into(profilePic);
                }
            } else {
                Exception error = result.getError();
                if (error != null) {
                    AndroidUtil.showToast(getContext(), "Cropping error: " + error.getMessage());
                }
            }

            cropContainerLayout.setVisibility(View.GONE);
            toggleMainActivityUi(View.VISIBLE);
        });

        updateProfileBtn.setOnClickListener(view1 -> {
            updateBtnClick();
        });

        logoutBtn.setOnClickListener(view1 -> {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tokenTask -> {
                if (tokenTask.isSuccessful() && tokenTask.getResult() != null) {
                    FirebaseUtil.currentUserDetails().update("fcmToken", null);
                }
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(fcmTask -> {
                    executeLocalLogout();
                });
            });
        });

        return view;
    }

    private void setupOccupationAndMechina() {
        if (getContext() == null) return;

        // Load Mechina Data
        fullMechinaList = JsonReader.convertJsonToObject(getContext());
        for (Mechina mechina : fullMechinaList) {
            allMechinotNames.add(mechina.getName());
        }

        // Set up Adapters
        ArrayAdapter<String> mechinaAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, allMechinotNames);
        mechinaSpinner.setAdapter(mechinaAdapter);

        ArrayAdapter<CharSequence> occupationAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.occupation_array, android.R.layout.simple_list_item_1);
        occupationSpinner.setAdapter(occupationAdapter);

        // Setup Logic Listeners
        occupationSpinner.setOnItemClickListener((parent, v, position, id) -> {
            String selectedValue = (String) parent.getItemAtPosition(position);

            if (selectedValue.equals("מכיניסט") || selectedValue.equals("ר\"מ או מדריך")) {
                mechinaSpinner.setText("");
                mechinaLayout.setVisibility(View.VISIBLE);
            } else {
                mechinaLayout.setVisibility(View.GONE);
                mechinaSpinner.setText("תיכון");
                mechinaLayout.setError(null);
            }
        });

        mechinaSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String userInput = mechinaSpinner.getText().toString();
                if (!userInput.isEmpty() && !allMechinotNames.contains(userInput)) {
                    mechinaLayout.setError("Please select a valid Mechina from the list");
                    mechinaSpinner.setText("");
                } else {
                    mechinaLayout.setError(null);
                }
            }
        });

        mechinaSpinner.setOnItemClickListener((parent, v, position, id) -> mechinaLayout.setError(null));
    }

    private void executeLocalLogout() {
        FirebaseUtil.logout();

        Intent intent = new Intent(getContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                getActivity().overrideActivityTransition(android.app.Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0);
            } else {
                getActivity().overridePendingTransition(0, 0);
            }
        }
    }

    private void toggleMainActivityUi(int visibility) {
        if (getActivity() != null) {
            View toolbar = getActivity().findViewById(R.id.main_toolbar);
            View divider = getActivity().findViewById(R.id.divider);
            View bottomNav = getActivity().findViewById(R.id.bottom_navigation);

            if (toolbar != null) toolbar.setVisibility(visibility);
            if (divider != null) divider.setVisibility(visibility);
            if (bottomNav != null) bottomNav.setVisibility(visibility);
        }
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString().trim();
        String newOccupation = occupationSpinner.getText().toString().trim();
        String newMechina = mechinaSpinner.getText().toString().trim();

        // Validate Username
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username should be at least 3 characters long");
            return;
        }

        // Validate Occupation
        if (newOccupation.isEmpty()) {
            occupationLayout.setError("Please select an occupation");
            return;
        } else {
            occupationLayout.setError(null);
        }

        // Validate Mechina
        if (mechinaLayout.getVisibility() == View.VISIBLE) {
            if (newMechina.isEmpty()) {
                mechinaLayout.setError("Please select a mechina");
                return;
            }
            if (!allMechinotNames.contains(newMechina)) {
                mechinaLayout.setError("Please select a valid Mechina from the list");
                mechinaSpinner.setText("");
                return;
            }
        }

        // Apply changes to the model
        currentUserModel.setUsername(newUsername);
        currentUserModel.setOccupation(newOccupation);

        if (mechinaLayout.getVisibility() == View.VISIBLE) {
            currentUserModel.setMechina(newMechina);
        } else {
            currentUserModel.setMechina("תיכון");
        }

        setInProgress(true);

        if (croppedImageUri != null) {
            StorageReference fileRef = FirebaseUtil.getCurrentProfilePicRef();
            fileRef.putFile(croppedImageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            fileRef.getDownloadUrl().addOnCompleteListener(urlTask -> {
                                if (urlTask.isSuccessful() && urlTask.getResult() != null) {
                                    String downloadUrl = urlTask.getResult().toString();
                                    currentUserModel.setProfilePicUrl(downloadUrl);
                                }
                                updateToFirestore();
                            });
                        } else {
                            setInProgress(false);
                            AndroidUtil.showToast(getContext(), "Failed to upload image");
                        }
                    });
        } else {
            updateToFirestore();
        }
    }

    void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Profile updated successfully");
                    } else {
                        AndroidUtil.showToast(getContext(), task.getException().getMessage());
                    }
                });
    }

    void getUserData() {
        setInProgress(true);

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (!isAdded() || getContext() == null) {
                return;
            }

            setInProgress(false);
            if (task.isSuccessful() && task.getResult() != null) {
                currentUserModel = task.getResult().toObject(UserModel.class);
                if (currentUserModel != null) {
                    usernameInput.setText(currentUserModel.getUsername());
                    emailInput.setText(currentUserModel.getEmail());

                    // Pre-fill Occupation and Mechina logic
                    String occupation = currentUserModel.getOccupation();
                    if (occupation != null) {
                        occupationSpinner.setText(occupation, false);
                        // FIX: Explicitly clear the adapter filter so all options remain available
                        if (occupationSpinner.getAdapter() != null) {
                            ((ArrayAdapter<?>) occupationSpinner.getAdapter()).getFilter().filter(null);
                        }

                        if (occupation.equals("מכיניסט") || occupation.equals("ר\"מ או מדריך")) {
                            mechinaLayout.setVisibility(View.VISIBLE);
                            mechinaSpinner.setText(currentUserModel.getMechina(), false);
                            // FIX: Explicitly clear the mechina adapter filter too
                            if (mechinaSpinner.getAdapter() != null) {
                                ((ArrayAdapter<?>) mechinaSpinner.getAdapter()).getFilter().filter(null);
                            }
                        } else {
                            mechinaLayout.setVisibility(View.GONE);
                        }
                    }

                    if (currentUserModel.getProfilePicUrl() != null && !currentUserModel.getProfilePicUrl().isEmpty()) {
                        Uri imageUri = Uri.parse(currentUserModel.getProfilePicUrl());
                        AndroidUtil.setProfilePic(getContext(), imageUri, profilePic);
                    }
                }
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}