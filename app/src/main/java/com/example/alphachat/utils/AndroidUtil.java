package com.example.alphachat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import com.bumptech.glide.Glide;
import com.example.alphachat.model.UserModel;


public class AndroidUtil {

    public static void showToast (Context context, String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("email", model.getEmail());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("profilePicUrl", model.getProfilePicUrl());
    }

    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setProfilePicUrl(intent.getStringExtra("profilePicUrl"));
        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).circleCrop().into(imageView);
    }

    public static void setErrorOnSearchView(SearchView searchView, String errorMessage) {
        if (searchView != null) {
            // Find the view using the exact AndroidX library ID, but reference it as a standard EditText
            EditText searchTextField = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

            if (searchTextField != null) {
                searchTextField.setError(errorMessage);
            }
        }
    }

}
