package com.jatora.tfg_the_climb_within;

import android.content.Context;
import android.content.SharedPreferences;

public class LanguagePreference {

    private static final String PREF_NAME = "LanguagePreferences";
    private static final String KEY_LANGUAGE = "language";
    private static final String DEFAULT_LANGUAGE = "en";  // Default to English

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    // Constructor
    public LanguagePreference(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save language preference
    public void setLanguage(String languageCode) {
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();
    }

    // Get language preference
    public String getLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
    }

    // Clear language preference
    public void clearLanguage() {
        editor.remove(KEY_LANGUAGE);
        editor.apply();
    }
}
