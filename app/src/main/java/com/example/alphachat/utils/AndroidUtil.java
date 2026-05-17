package com.example.alphachat.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class AndroidUtil {

    public static void showToast (Context context, String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).circleCrop().into(imageView);
    }

}
