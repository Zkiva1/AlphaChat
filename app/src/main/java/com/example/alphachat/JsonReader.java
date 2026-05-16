package com.example.alphachat;

import android.content.Context;
import com.example.alphachat.model.Mechina;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonReader {

    // 1. Changed return type from 'Mechina' to 'List<Mechina>'
    public static List<Mechina> convertJsonToObject(Context context) {

        // Use a try-with-resources statement to guarantee the stream closes automatically
        try (InputStream inputStream = context.getResources().openRawResource(R.raw.mechinot)) {

            int size = inputStream.available();
            byte[] data = new byte[size];
            inputStream.read(data);

            String jsonString = new String(data, StandardCharsets.UTF_8);

            // 2. Instruct Gson that it is parsing a List of Mechina objects
            return new Gson().fromJson(jsonString, new TypeToken<ArrayList<Mechina>>(){}.getType());

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list if loading fails to prevent crashes elsewhere
        }
    }
}