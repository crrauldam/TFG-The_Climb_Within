package com.jatora.tfg_the_climb_within;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.view.WindowManager;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    // constants for game files
    private static final String SAVE_FILE = "save.json";

    // DONE: CHANGE THIS DATA TO "DEFAULT_SAVE_DATA" AND CHANGE CONTENT TO **REAL** DEFAULT SAVE DATA
    private static final String DEFAULT_SAVE_DATA = "{\n" +
            "    \"player\": {\n" +
            "        \"isFirstTime\": true,\n" +
            "        \"timestamp\": 0,\n" +
            "        \"name\": \"PlayerName\",\n" +
            "        \"maxhp\": 100,\n" +
            "        \"hp\": 100,\n" +
//            this is for tests
//            "        \"unlocked_cards\": [1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009,  2000, 3000, 4000, 5000, 6000],\n" +
            "        \"unlocked_cards\": [1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009],\n" +
            "        \"tower_coins\": 0,\n" +
            "        \"unlocked_towers\": [0],\n" +
            "        \"emotion_coins\": {\n" +
            "            \"anger\": 0,\n" +
            "            \"disgust\": 0,\n" +
            "            \"fear\": 0,\n" +
            "            \"happiness\": 0,\n" +
            "            \"sadness\": 0,\n" +
            "            \"surprise\": 0\n" +
            "        },\n" +
            "        \"deck\": [],\n" +
            "        \"stats\": {\n" +
            "            \"played_time\": 0,\n" +
            "            \"cards_played\": 0,\n" +
            "            \"damage_dealt\": 0,\n" +
            "            \"damage_received\": 0\n" +
            "        },\n" +
            "        \"settings\": {\n" +
            "            \"language\": \"en\"\n" +
            "        }\n" +
            "    }\n" +
            "}";


    private static final String PROPERTIES_EN_FILE = "game_data/properties_en.json";
    private static final String PROPERTIES_ES_FILE = "game_data/properties_es.json";
    private static final String STORY_FILE = "game_data/story.json";
    private static final String TOWERS_FILE = "game_data/towers.json";
    private static final String ENEMIES_FILE = "game_data/enemies.json";
    private static final String DECKS_FILE = "game_data/decks.json";
    private static final String CARDS_FILE = "game_data/cards.json";

    // GSON object for the whole class (shouldn't have any problems it doesn't store data)
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Firebase
    private Firebase mAuth;

    public static void changeActivity(Activity from, Class to, int fromAnim, int toAnim) {
        Intent intent = new Intent(from, to);

        from.startActivity(intent);
        from.overridePendingTransition(toAnim, fromAnim);
    }

    public static void changeActivity(Intent intent, Activity from, int fromAnim, int toAnim) {
        from.startActivity(intent);
        from.overridePendingTransition(toAnim, fromAnim);
    }


    /**
     * Reads the given file from the assets folder.
     *
     * @param context
     * @param path
     * @return A String with the given file's content
     */
    public static String readFile(Context context, String path) {
        final String TAG = "Utils-readFile()";
        Log.d(TAG, "Retrieving file content: " + path);

        // fetch file
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(path)));
        } catch (IOException e) {
            Log.e(TAG, "Error while opening file: " + e);
        }
        StringBuilder fileContent = new StringBuilder();
        String line;

        try {
            while ((line = br.readLine()) != null) {
                line.split(";");
                fileContent.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while reading file: " + e);
        }

        try {
            br.close();
        } catch (IOException e) {
            Log.e(TAG, "Error while closing BufferedReader: " + e);
        }

        return fileContent.toString();
    }

    /**
     * Retrieve language of device. If language is not set, it defaults to English.
     *
     * @return 0 = English | 1 = Spanish
     */
    public static int getLang(Context context) {
        final String TAG = "Utils-getLang()";
        int lang = 0;

        LanguagePreference languagePreference = new LanguagePreference(context);

        Log.d(TAG, "Retrieving language from system properties.");
        // if spanish, change language to spanish, if not it stays in english
        if (languagePreference.getLanguage().equalsIgnoreCase("es")) {
            lang = 1;
        }

        return lang;
    }

    /**
     * Get the properties of the game, returns data in device's language.
     * See {@link #getLang(Context context)} for language retrieving.
     *
     * @return A JsonObject containing the properties info in the specified language
     */
    public static JsonObject getProperties(Context context) {
        final String TAG = "Utils-getProperties()";

        Log.d(TAG, "Player language: " + PlayerManager.getInstance(context).getSettings().getLanguage());

        String file = (getLang(context) == 1) ? PROPERTIES_ES_FILE : PROPERTIES_EN_FILE;

        String fileData = Utils.readFile(context, file);

        return gson.fromJson(fileData, JsonObject.class);
    }

    public static void checkPlayerSignedIn(Context context) {

    }

    public static void initiateFirebaseLoginSequence(Context context, FirebaseUser user) {
        final String TAG = "Utils-initiateFirebaseLoginSequence";
        if(user != null) {
            Log.d(TAG, "User (" + user + ") was found.");
//            startActivity(new Intent(this, Home.class));
//            finish();
        }
        else {
            Log.d(TAG, "User was NOT found: " + user);
//            startActivity(new Intent(this, Login.class));
//            finish();
        }
    }

    public static ArrayList<Tower> getTowersData(Context context) {
        final String TAG = "Utils-getTowersData()";

        // get file's content as String
        String towersData = Utils.readFile(context, TOWERS_FILE);

        // parse tower data to JSON
        JsonObject towerJSON = gson.fromJson(towersData, JsonObject.class);
        // get towers array located in object with key "towers"
        JsonArray towerArray = towerJSON.getAsJsonArray("towers");
        // ArrayList for storing towers
        ArrayList<Tower> towers = new ArrayList<>();

        // get properties data
        JsonObject propertiesENJSON = getProperties(context);
        // get object with tower properties from inside properties (en) JSON object, located with key "towers"
        JsonObject towerProperties = propertiesENJSON.getAsJsonObject("towers");

        // loop through all elements in the JSON array with towers's info
        for (JsonElement je : towerArray) {
            Log.d("A", je.toString());
            // parse JSON object to Tower Java object
            Tower tower = gson.fromJson(je.toString(), Tower.class);
            // replace initial name (towerN.name) with property stored in the properties data
            tower.setName(towerProperties.get(tower.getName()).getAsString());
            tower.setImg(towerProperties.get(tower.getImg()).getAsString());

            Log.e(TAG, "tower img: " + tower.getImg());

            towers.add(tower);
        }

        return towers;
    }


    public static ArrayList<Enemy> getEnemiesData(Context context) {
        final String TAG = "Utils-getEnemiesData()";

        // get file's content as String
        String enemiesData = Utils.readFile(context, ENEMIES_FILE);

        // parse enemies data to JSON
        JsonObject enemiesJSON = gson.fromJson(enemiesData, JsonObject.class);

        // ArrayList for storing towers
        ArrayList<Enemy> enemies = new ArrayList<>();

        // get properties data
        JsonObject propertiesENJSON = getProperties(context);
        // get object with enemies properties from inside properties (en) JSON object, located with key "enemies"
        JsonObject enemiesProperties = propertiesENJSON.getAsJsonObject("enemies");

        for (String key : enemiesJSON.keySet()) {
            Log.d(TAG, "checking enemy: " + key);

            Enemy enemy = gson.fromJson(enemiesJSON.get(key), Enemy.class);
            enemy.setName(enemiesProperties.get(enemy.getName()).getAsString());
            enemy.setImg(enemiesProperties.get(enemy.getImg()).getAsString());

            enemies.add(enemy);
        }

        return enemies;
    }


    /**
     * Checks if the save.json file exists in the internal storage, if not, it creates it.
     * @param context
     */
    public static void checkSaveFileExistence(Context context) {
        final String TAG = "Utils-checkSaveFileExistence";

        File file = new File(context.getFilesDir(), "save.json");

        if (!file.exists()) {
            Log.d(TAG, "creating and inserting default data into save.json in internal storage");
            try (FileOutputStream fos = context.openFileOutput(SAVE_FILE, Context.MODE_PRIVATE)) {
                fos.write(DEFAULT_SAVE_DATA.getBytes());
            } catch (IOException e) {
                Log.e(TAG, "Failed to create save.json");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * FOR TESTING PURPOSES, RESETS THE SAVE FILE CONTENT TO DEFAULT
     *
     * @param context
     */
    public static void resetSaveFileContent(Context context) {
        final String TAG = "Utils-resetSaveFileContent";

        File file = new File(context.getFilesDir(), "save.json");

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Log.d(TAG, "resetting data into save.json in internal storage");
        try (FileOutputStream fos = context.openFileOutput(SAVE_FILE, Context.MODE_PRIVATE)) {
            fos.write(DEFAULT_SAVE_DATA.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Failed to reset save.json");
            throw new RuntimeException(e);
        }
    }


    public static ArrayList<Card> getCardsData(Context context) {
        final String TAG = "Utils-getCardsData()";

        // get file's content as String
        String cardsData = Utils.readFile(context, CARDS_FILE);

        // parse cards data to JSON
        JsonObject cardsJSON = gson.fromJson(cardsData, JsonObject.class);

        // ArrayList for storing towers
        ArrayList<Card> cards = new ArrayList<>();

        // get properties data
        JsonObject propertiesENJSON = getProperties(context);

        // get object with cards properties from inside properties (en) JSON object, located with key "cards"
        JsonObject cardsProperties = propertiesENJSON.getAsJsonObject("cards");

        for (String key : cardsJSON.keySet()) {
            Log.d(TAG, "checking card (ID): " + key);
            Log.d(TAG, "checking card (DATA): " + cardsJSON.get(key));

            Card card = gson.fromJson(cardsJSON.get(key), Card.class);
            card.setName(cardsProperties.get(card.getName()).getAsString());
            card.setDescription(cardsProperties.get(card.getDescription()).getAsString());
            card.setIcon(cardsProperties.get(card.getIcon()).getAsString());

            cards.add(card);
        }

        return cards;
    }


    public static ArrayList<Deck> getDecksData(Context context) {
        final String TAG = "Utils-getDecksData()";

        // get file's content as String
        String decksData = Utils.readFile(context, DECKS_FILE);

        // parse decks data to JSON
        JsonObject decksJSON = gson.fromJson(decksData, JsonObject.class);

        // ArrayList for storing towers
        ArrayList<Deck> decks = new ArrayList<>();

        for (String key : decksJSON.keySet()) {
            Log.d(TAG, "checking deck: " + key);

            Deck deck = gson.fromJson(decksJSON.get(key), Deck.class);
            deck.setName(key);

            decks.add(deck);
        }

        return decks;
    }


    public static ArrayList<DialogueSet> getStoryData(Context context) {
        final String TAG = "Utils-getStoryData()";

        // get file's content as String
        String storyData = Utils.readFile(context, STORY_FILE);

        // parse story data to JSON
        JsonObject storyJSON = gson.fromJson(storyData, JsonObject.class);

        // get properties data
        JsonObject propertiesENJSON = getProperties(context);

        // get object with story properties from inside properties (en) JSON object, located with key "story"
        JsonObject storyProperties = propertiesENJSON.getAsJsonObject("story");

        ArrayList<DialogueSet> story = new ArrayList<>();

        // loop through story.json keys
        for (String key : storyJSON.keySet()) {
            // set with all the dialogues of each part of the story (intro, towers...)
            DialogueSet dialogueSet = new DialogueSet();
            // stores only dialogues
            ArrayList<String> dialogues = new ArrayList<>();
            // gets all dialogues of that particular key (=part of the story)
            JsonObject dialoguesJSON = storyProperties.get(key).getAsJsonObject();

            // loops through each dialogue of it
            for (String dialogueID : dialoguesJSON.keySet()) {
                // adds each dialogue to the arraylist
                dialogues.add(dialoguesJSON.get(dialogueID).getAsString());
            }

            // sets the DialogueSet info with the retrieved data
            dialogueSet.setId(key);
            dialogueSet.setDialogues(dialogues);

            // adds set to story list
            story.add(dialogueSet);
        }

        return story;
    }


    /**
     * Actual playing of the animation.
     *
     * @param targetView
     */
    public static void playFadeInFadeOutAnimation(View targetView, long fadeDuration, long waitDuration) {
        final String TAG = "BattleScreen-playFadeInOutAnimation";

        Log.d(TAG, "Setting animation.");

        // setting fade-in animation
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(targetView, "alpha", 0f, 1f);
        fadeIn.setDuration(fadeDuration);

        // setting wait animation (won't do anything but has to be done for the object to stay the same Xms)
        ObjectAnimator wait = ObjectAnimator.ofFloat(targetView, "alpha", 1f, 1f);
        wait.setDuration(waitDuration);

        // setting fade-out animation
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(targetView, "alpha", 1f, 0f);
        fadeOut.setDuration(fadeDuration);

        // creating the set of animations we'll play in a specific order
        AnimatorSet animatorSet = new AnimatorSet(); // creation
        animatorSet.playSequentially(fadeIn, wait, fadeOut);
        Log.d(TAG, "Playing animation.");
        animatorSet.start();
    }

    /**
     * Play story narration for target part.
     * @param context
     * @param storyNarrationBackground
     * @param storyNarrationView
     * @param target
     */
    public static void playStoryNarration(Context context, ConstraintLayout storyNarrationBackground, TextView storyNarrationView, String target, Callback_TCW callback) {
        final String TAG = "BattleScreen-playStoryNarration";

        ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ArrayList<DialogueSet> storyData = getStoryData(context);
        DialogueSet narration = storyData.stream().filter(ds -> ds.getId().equalsIgnoreCase(target)).findFirst().orElse(new DialogueSet());
//        // TEST: REMOVE WHEN NOT IN TESTING, LINE ABOVE IS FOR REAL GAME
//        DialogueSet narration = storyData.stream().filter(ds -> ds.getId().equalsIgnoreCase("intro")).findFirst().orElse(new DialogueSet());

        // change from gone to visible (though alpha is 0 for appearing effect)
        storyNarrationBackground.setVisibility(View.VISIBLE);
        storyNarrationBackground.setAlpha(0f);
        // fade in from invisible to visible
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(storyNarrationBackground, "alpha", 0f, 1f);
        fadeIn.setDuration(700);
        fadeIn.start();

        new Handler().postDelayed(() -> {
            playDialogueSequence(context, storyNarrationBackground, storyNarrationView, narration.getDialogues(), 0, callback);
        }, 700);
    }

    private static void playDialogueSequence(Context context, ConstraintLayout storyNarrationBackground, TextView storyNarrationView, List<String> dialogues, int index, Callback_TCW callback) {
        long fadeDuration = 700;
        long waitDuration = 3000;

        if (index >= dialogues.size()) {
            // Once all dialogues are shown, fade out the background and hide it
            ObjectAnimator fadeOutBackground = ObjectAnimator.ofFloat(storyNarrationBackground, "alpha", 1f, 0f);
            fadeOutBackground.setDuration(fadeDuration);
            fadeOutBackground.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    storyNarrationBackground.setVisibility(View.GONE); // Hide it after fading out
                }
            });
            fadeOutBackground.start();
            // since all dialogues have been shown, call the onSuccess() method on callback
            callback.onSuccess(context);
            return; // exit when all dialogues are shown

        }


        storyNarrationView.setText(dialogues.get(index));

        // Create fade-in animation
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(storyNarrationView, "alpha", 0f, 1f);
        fadeIn.setDuration(fadeDuration);

        // Create fade-out animation
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(storyNarrationView, "alpha", 1f, 0f);
        fadeOut.setDuration(fadeDuration);
        fadeOut.setStartDelay(waitDuration);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(fadeIn, fadeOut);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                playDialogueSequence(context, storyNarrationBackground, storyNarrationView, dialogues, index + 1, callback); // Call next dialogue
            }
        });

        set.start();
    }
}
