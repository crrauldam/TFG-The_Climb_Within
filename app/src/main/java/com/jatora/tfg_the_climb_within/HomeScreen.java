package com.jatora.tfg_the_climb_within;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {
    private LanguagePreference languagePreference;
    private String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        languagePreference = new LanguagePreference(this);
//        currentLanguage = languagePreference.getLanguage();  // Save the initial language
//
//        applyLanguage();  // Apply saved language

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        View touchToStart = findViewById(R.id.touchToStart);
        TextView touchToStartGuide = findViewById(R.id.touchToStartGuide);
        ImageButton shopButton = findViewById(R.id.shop);
        ImageButton yourCardsButton = findViewById(R.id.yourCards);
        AppCompatButton settingsButton = findViewById(R.id.settingsButton);
        AppCompatButton faqButton = findViewById(R.id.faq);

        // Move up animation
        ObjectAnimator moveUp = ObjectAnimator.ofFloat(touchToStartGuide, "translationY", 0f, -30f);
        moveUp.setDuration(1000);
        moveUp.setInterpolator(new LinearInterpolator());

        // Move down animation
        ObjectAnimator moveDown = ObjectAnimator.ofFloat(touchToStartGuide, "translationY", -30f, 0f);
        moveDown.setDuration(1000);
        moveDown.setInterpolator(new LinearInterpolator());

        // Combine them in a sequence
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveUp, moveDown);
        animatorSet.setStartDelay(200);  // Optional: Pause between loops

        // Loop the animation infinitely
        animatorSet.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                animatorSet.start();  // Restart after end
            }

            @Override
            public void onAnimationStart(android.animation.Animator animation) {
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {
            }

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {
            }
        });

        animatorSet.start();

        touchToStart.setOnClickListener(v -> {
            touchToStart.setClickable(false);
            touchToStart.postDelayed(() -> touchToStart.setClickable(true), 1000);

            PlayerManager.checkRemotePlayerData(this, mAuth.getCurrentUser());
            Utils.changeActivity(this, TowerSelection.class, R.anim.slide_out_left, R.anim.slide_in_right);
        });

        yourCardsButton.setOnClickListener(v -> {
            yourCardsButton.setClickable(false);
            yourCardsButton.postDelayed(() -> yourCardsButton.setClickable(true), 1000);

            PlayerManager.checkRemotePlayerData(this, mAuth.getCurrentUser());
            Utils.changeActivity(this, YourCards.class, R.anim.slide_out_top, R.anim.slide_in_bottom);
        });

        shopButton.setOnClickListener(v -> {
            shopButton.setClickable(false);
            shopButton.postDelayed(() -> shopButton.setClickable(true), 1000);

            PlayerManager.checkRemotePlayerData(this, mAuth.getCurrentUser());
            Utils.changeActivity(this, Shop.class, R.anim.slide_out_right, R.anim.slide_in_left);
        });

        settingsButton.setOnClickListener(v -> {
            settingsButton.setClickable(false);
            settingsButton.postDelayed(() -> settingsButton.setClickable(true), 1000);

            Utils.changeActivity(this, Settings.class, R.anim.slide_out_bottom, R.anim.slide_in_top);
        });

        faqButton.setOnClickListener(v -> {
            faqButton.setClickable(false);
            faqButton.postDelayed(() -> faqButton.setClickable(true), 1000);

            openTutorialDialog();
        });
    }

    private void openTutorialDialog() {
        TutorialDialogFragment tutorialDialog = new TutorialDialogFragment();
        tutorialDialog.show(getSupportFragmentManager(), "TutorialDialog");
    }
//
//    // for dynamic language change
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // Check if language preference has changed
//        String newLanguage = languagePreference.getLanguage();
//        if (!newLanguage.equals(currentLanguage)) {
//            currentLanguage = newLanguage;  // Update the current language
//            recreate();  // Reload the activity to apply language change
//        }
//    }
//
//    private void applyLanguage() {
//        String languageCode = languagePreference.getLanguage();
//        LocaleHelper.updateLocale(this, languageCode);
//    }
//
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        LanguagePreference pref = new LanguagePreference(newBase);
//        super.attachBaseContext(LocaleHelper.updateLocale(newBase, pref.getLanguage()));
//    }
}