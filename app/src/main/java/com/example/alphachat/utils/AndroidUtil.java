package com.example.alphachat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.alphachat.model.Mechina;
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

    public static void passMechinaModelAsIntent(Intent intent, Mechina model) {
        intent.putExtra("name", model.getName());
        intent.putExtra("gender", model.getGender());
        intent.putExtra("image", model.getImage());
        intent.putExtra("link", model.getLink());
        intent.putExtra("type", model.getType());
        intent.putExtra("region", model.getRegion());
    }

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

    public static class SafeLinearLayoutManager extends LinearLayoutManager {
        public SafeLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                // Catch the crash and ignore it.
            }
        }
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).circleCrop().into(imageView);
    }

}
