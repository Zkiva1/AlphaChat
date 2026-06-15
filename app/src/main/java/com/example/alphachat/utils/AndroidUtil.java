package com.example.alphachat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.alphachat.model.Mechina;
import com.example.alphachat.model.UserModel;

/**
 * Utility class for common Android-specific operations.
 *
 * This class provides helper methods for UI notifications, Intent data passing,
 * profile image loading, and custom layout management. It simplifies repetitive tasks
 * related to the Android framework.
 */
public class AndroidUtil {

    /**
     * Displays a long-duration Toast message to the user.
     *
     * @param context The {@link Context} used to create the Toast.
     * @param message The text message to display.
     */
    public static void showToast (Context context, String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    /**
     * Packs {@link UserModel} data into an {@link Intent} for Activity transitions.
     *
     * @param intent The {@link Intent} to populate with data.
     * @param model The {@link UserModel} containing the data to pass.
     */
    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("email", model.getEmail());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("profilePicUrl", model.getProfilePicUrl());
    }

    /**
     * Extracts {@link UserModel} data from an {@link Intent}.
     *
     * @param intent The {@link Intent} containing the user data extras.
     * @return A {@link UserModel} populated with the data from the intent.
     */
    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setProfilePicUrl(intent.getStringExtra("profilePicUrl"));
        return userModel;
    }

    /**
     * Packs {@link Mechina} data into an {@link Intent} for Activity transitions.
     *
     * @param intent The {@link Intent} to populate with data.
     * @param model The {@link Mechina} model containing the academy details.
     */
    public static void passMechinaModelAsIntent(Intent intent, Mechina model) {
        intent.putExtra("name", model.getName());
        intent.putExtra("gender", model.getGender());
        intent.putExtra("image", model.getImage());
        intent.putExtra("link", model.getLink());
        intent.putExtra("type", model.getType());
        intent.putExtra("region", model.getRegion());
    }

    /**
     * Extracts {@link Mechina} data from an {@link Intent}.
     *
     * @param intent The {@link Intent} containing the Mechina data extras.
     * @return A {@link Mechina} model populated with the data from the intent.
     */
    public static Mechina getMechinaModelFromIntent(Intent intent) {
        Mechina mechinaModel = new Mechina();
        mechinaModel.setName(intent.getStringExtra("name"));
        mechinaModel.setGender(intent.getStringExtra("gender"));
        mechinaModel.setImage(intent.getStringExtra("image"));
        mechinaModel.setLink(intent.getStringExtra("link"));
        mechinaModel.setType(intent.getStringExtra("type"));
        mechinaModel.setRegion(intent.getStringExtra("region"));
        return mechinaModel;
    }

    /**
     * A {@link LinearLayoutManager} that safely handles inconsistent data updates.
     *
     * This class overrides {@code onLayoutChildren} to catch {@link IndexOutOfBoundsException}
     * which can occur in {@link RecyclerView} during rapid data changes or animations.
     */
    public static class SafeLinearLayoutManager extends LinearLayoutManager {
        /**
         * Constructs a new SafeLinearLayoutManager.
         *
         * @param context The application or activity context.
         */
        public SafeLinearLayoutManager(Context context) {
            super(context);
        }

        /**
         * Lays out all relevant child views from the adapter.
         *
         * @param recycler The {@link RecyclerView.Recycler} to use.
         * @param state The current {@link RecyclerView.State}.
         */
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                // Catch the crash and ignore it.
            }
        }
    }

    /**
     * Loads and crops a profile picture into an {@link ImageView}.
     *
     * Uses the Glide library to load an image from a {@link Uri}, apply a
     * circular crop, and display it in the target view.
     *
     * @param context The {@link Context} for Glide.
     * @param imageUri The {@link Uri} of the image to load.
     * @param imageView The {@link ImageView} where the image will be displayed.
     */
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).circleCrop().into(imageView);
    }

}
