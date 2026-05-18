package com.example.alphachat.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import com.bumptech.glide.Glide;


public class AndroidUtil {

    public static void showToast (Context context, String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
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
