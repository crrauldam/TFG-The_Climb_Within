package com.jatora.tfg_the_climb_within;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PlayerManager {
    private static final String SAVE_FILE = "save.json";
    private static Player instance;
    // GSON object for the whole class (shouldn't have any problems it doesn't store data)
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();


    private PlayerManager() {
        // Private constructor to prevent external instantiation
    }

    public static synchronized Player getInstance(Context context) {
        if (instance == null) {
            instance = loadPlayerData(context); // Load data only if it's null
        }
        return instance;
    }

    private static Player loadPlayerData(Context context) {
        Player player = new Player(); // Create a new Player object

        final String TAG = "PlayerManager-loadPlayerData()";

        StringBuilder sb = new StringBuilder();

        try (FileInputStream fis = context.openFileInput(SAVE_FILE)) {
            int ch;
            while ((ch = fis.read()) != -1) {
                sb.append((char) ch);
            }
        } catch (IOException e) {
            Log.e(TAG, "error reading player data");
            return null;
        }

        // parse player data to JSON
        JsonObject playerJSON = gson.fromJson(sb.toString(), JsonObject.class);

        Log.d(TAG, "Getting player's saved data: "+playerJSON);

        player = gson.fromJson(playerJSON.get("player"), Player.class);

        Log.d(TAG, "player state: "+player);

        return player;
    }

    public static void savePlayerData(Context context, Player playerObject) {
        final String TAG = "PlayerManager-savePlayerData";

        Log.d(TAG, "parsing player object into string and adding to jsonobject.");
        JsonObject jo = new JsonObject();
        jo.add("player", gson.toJsonTree(playerObject));

        Log.d(TAG, "Writing into file: "+SAVE_FILE);

        try (FileOutputStream fos = context.openFileOutput(SAVE_FILE, Context.MODE_PRIVATE)) {
            fos.write(jo.toString().getBytes());
            Log.d(TAG, "Successfully wrote into file.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to write into the file.");
            e.printStackTrace();
        }
    }
}