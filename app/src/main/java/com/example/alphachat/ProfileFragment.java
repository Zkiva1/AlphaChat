package com.example.alphachat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageView;
import com.example.alphachat.model.UserModel;
import com.example.alphachat.utils.AndroidUtil;
import com.example.alphachat.utils.FirebaseUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput, emailInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    LinearLayout cropContainerLayout;
    CropImageView cropImageView;
    Button btnCancelCrop, btnConfirmCrop;
    Uri croppedImageUri;

    UserModel currentUserModel;

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
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        emailInput = view.findViewById(R.id.profile_email);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        cropContainerLayout = view.findViewById(R.id.crop_container_layout);
        cropImageView = view.findViewById(R.id.crop_image_view);
        btnCancelCrop = view.findViewById(R.id.btn_cancel_crop);
        btnConfirmCrop = view.findViewById(R.id.btn_confirm_crop);

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
            // 1. Get the current token first so we know what to remove from Firestore
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tokenTask -> {

                if (tokenTask.isSuccessful() && tokenTask.getResult() != null) {
                    String currentToken = tokenTask.getResult();

                    // 2. Remove the token from your Firestore array before signing out
                    // (If you used a single String field instead of an array, use: .update("fcmToken", null))
                    FirebaseUtil.currentUserDetails()
                            .update("fcmToken", null);
                }

                // 3. Now trigger the token deletion from the device
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(fcmTask -> {
                    // Even if the network call fails, we MUST let the user log out locally.
                    executeLocalLogout();
                });
            });
        });

        return view;
    }

    private void executeLocalLogout() {
        FirebaseUtil.logout();

        Intent intent = new Intent(getContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            // Safe transition handling for both older and newer Android APIs
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
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username should be at least 3 characters long");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);

        if (croppedImageUri != null) {
            StorageReference fileRef = FirebaseUtil.getCurrentProfilePicRef();

            fileRef.putFile(croppedImageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AndroidUtil.showToast(getContext(), "Updated profile picture");

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
                        AndroidUtil.showToast(getContext(), "Updated username");
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