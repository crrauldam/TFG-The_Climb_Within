package com.jatora.tfg_the_climb_within;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Utils {
    // constants for game files
    private static final String SAVE_FILE = "save.json";
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

    public static void changeActivity(Activity from, Class to, int fromAnim, int toAnim) {
        Intent intent = new Intent(from, to);

        from.startActivity(intent);
        from.overridePendingTransition(toAnim, fromAnim);
    }


    /**
     * Reads the given file from the assets folder.
     * @param context
     * @param path
     * @return A String with the given file's content
     */
    public static String readFile(Context context, String path) {
        Log.d("Utils-readFile()", "Retrieving file content: "+path);

        // fetch file
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(path)));
        } catch (IOException e) {
            Log.e("Utils-readFile()", "Error while opening file: " + e);
        }
        StringBuilder fileContent = new StringBuilder();
        String line;

        try {
            while ((line = br.readLine()) != null) {
                line.split(";");
                fileContent.append(line);
            }
        } catch (IOException e) {
            Log.e("Utils-readFile()", "Error while reading file: " + e);
        }

        try {
            br.close();
        } catch (IOException e) {
            Log.e("Utils-readFile()", "Error while closing BufferedReader: " + e);
        }

        return fileContent.toString();
    }


    /**
     * Get the properties in the specified language:
     * <ul>
     *     <li>0 for English</li>
     *     <li>1 for Spanish</li>
     * </ul>
     * @param lang
     * @return A JsonObject containing the properties info in the specified language
     */
    public static JsonObject getProperties(Context context, int lang) {
        String file = (lang == 1) ? PROPERTIES_ES_FILE : PROPERTIES_EN_FILE;

        String fileData = Utils.readFile(context, file);

        return gson.fromJson(fileData, JsonObject.class);
    }


    public static ArrayList<Tower> getTowersData(Context context) {
        // get file's content as String
        String towersData = Utils.readFile(context, TOWERS_FILE);

        // parse tower data to JSON
        JsonObject towerJSON = gson.fromJson(towersData, JsonObject.class);
        // get towers array located in object with key "towers"
        JsonArray towerArray = towerJSON.getAsJsonArray("towers");
        // ArrayList for storing towers
        ArrayList<Tower> towers = new ArrayList<>();

        // get properties data
        JsonObject propertiesENJSON = getProperties(context, 0);
        // get object with tower properties from inside properties (en) JSON object, located with key "towers"
        JsonObject towerProperties = propertiesENJSON.getAsJsonObject("towers");

        // loop through all elements in the JSON array with towers's info
        for (JsonElement je : towerArray) {
            Log.d("A", je.toString());
            // parse JSON object to Tower Java object
            Tower tower = gson.fromJson(je.toString(), Tower.class);
            // replace initial name (towerN.name) with property stored in the properties data
            tower.setName(String.valueOf(towerProperties.get(tower.getName())));
//            tower.setImg(String.valueOf(towerProperties.get(tower.getImg())));

            Log.e("A", "onCreate: "+tower.getImg());

            towers.add(tower);
        }

        return towers;
    }

    public static ArrayList<Enemy> getEnemiesData(Context context) {
        // get file's content as String
        String enemiesData = Utils.readFile(context, ENEMIES_FILE);

        // parse enemies data to JSON
        JsonObject enemiesJSON = gson.fromJson(enemiesData, JsonObject.class);

        // ArrayList for storing towers
        ArrayList<Enemy> enemies = new ArrayList<>();

        // get properties data
        JsonObject propertiesENJSON = getProperties(context, 0);
        // get object with enemies properties from inside properties (en) JSON object, located with key "enemies"
        JsonObject enemiesProperties = propertiesENJSON.getAsJsonObject("enemies");

        for (String key : enemiesJSON.keySet()) {
            Log.d("A", "checking enemy: "+key);

            Enemy enemy = gson.fromJson(enemiesJSON.get(key).getAsString(), Enemy.class);
            enemy.setName(String.valueOf(enemiesProperties.get(enemy.getName())));
            enemy.setImg(String.valueOf(enemiesProperties.get(enemy.getImg())));

            enemies.add(enemy);
        }

        return enemies;
    }

    public static Player getPlayerData(Context context) {
        // get file's content as String
        String playerData = Utils.readFile(context, SAVE_FILE);

        // parse enemies data to JSON
        JsonObject playerJSON = gson.fromJson(playerData, JsonObject.class);

        Log.d("A", "Getting player's saved data: "+playerJSON);

        return gson.fromJson(playerJSON.get("player"), Player.class);
    }

    public static ArrayList<Card> getCardsData(Context context) {
        // get file's content as String
        String cardsData = Utils.readFile(context, CARDS_FILE);

        // parse enemies data to JSON
        JsonObject cardsJSON = gson.fromJson(cardsData, JsonObject.class);

        // ArrayList for storing towers
        ArrayList<Card> cards = new ArrayList<>();

        // get properties data
        JsonObject propertiesENJSON = getProperties(context, 0);
        // get object with enemies properties from inside properties (en) JSON object, located with key "enemies"
        JsonObject cardsProperties = propertiesENJSON.getAsJsonObject("cards");

        for (String key : cardsJSON.keySet()) {
            Log.d("Utils-getCardsData", "checking card: "+key);
            Log.d("Utils-getCardsData", "checking card: "+cardsJSON.get(key));


            Card card = gson.fromJson(cardsJSON.get(key), Card.class);
            card.setName(String.valueOf(cardsProperties.get(card.getName()).getAsString()));
            card.setDescription(String.valueOf(cardsProperties.get(card.getDescription()).getAsString()));
            card.setIcon(String.valueOf(cardsProperties.get(card.getIcon()).getAsString()));

            cards.add(card);
        }

        return cards;
    }

    public static ArrayList<Deck> getDecksData(Context context) {
        // get file's content as String
        String decksData = Utils.readFile(context, DECKS_FILE);

        // parse enemies data to JSON
        JsonObject decksJSON = gson.fromJson(decksData, JsonObject.class);

        // ArrayList for storing towers
        ArrayList<Deck> decks = new ArrayList<>();

        for (String key : decksJSON.keySet()) {
            Log.d("Utils-getDecksData", "checking deck: "+key);

            Deck deck = gson.fromJson(decksJSON.get(key), Deck.class);
            deck.setName(key);

            decks.add(deck);
        }

        return decks;
    }

    public static ArrayList<DialogueSet> getStoryData(Context context) {
        // get file's content as String
        String storyData = Utils.readFile(context, STORY_FILE);

        // parse enemies data to JSON
        JsonObject storyJSON = gson.fromJson(storyData, JsonObject.class);

        // get properties data
        JsonObject propertiesENJSON = getProperties(context, 0);
        // get object with enemies properties from inside properties (en) JSON object, located with key "enemies"
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
}
