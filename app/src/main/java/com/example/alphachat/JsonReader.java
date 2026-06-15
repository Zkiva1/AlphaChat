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

/**
 * Utility class for reading and deserializing academy data from a JSON resource.
 *
 * This class provides a helper method to parse the {@code mechinot.json} file located
 * in the raw resources folder into a list of {@link Mechina} objects using the Gson library.
 */
public class JsonReader {

    /**
     * Reads the academy data from {@code R.raw.mechinot} and converts it to a list of models.
     *
     * @param context The {@link Context} used to access application resources.
     * @return A {@link List} of {@link Mechina} objects, or an empty list if an error occurs.
     */
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