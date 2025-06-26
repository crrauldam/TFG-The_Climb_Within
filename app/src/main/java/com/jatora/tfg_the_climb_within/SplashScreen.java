package com.jatora.tfg_the_climb_within;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private FullScreenHelper fullScreenHelper;

    private FirebaseAuth mAuth;
    private final Handler handler= new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        fullScreenHelper = new FullScreenHelper(this);
        fullScreenHelper.enableFullScreen();

        final String TAG = "SplashScreen-onCreate";

        // Get the current user if user is logged in
        mAuth = FirebaseAuth.getInstance();

        // Get reference to the Lottie view
        LottieAnimationView lottieView = findViewById(R.id.animated_loading);

        // Add fade in animation to logo
        ImageView logo = findViewById(R.id.logo);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        logo.setAnimation(fadeInAnimation);

        // Check if dark mode is enabled
        boolean isDarkTheme = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;

        // Set animation resource based on theme
        if (isDarkTheme) {
            lottieView.setAnimation(R.raw.loading_light); // Use your dark animation
        } else {
            lottieView.setAnimation(R.raw.loading_dark); // Use your light animation
        }

        // Play the animation
        lottieView.playAnimation();

        runnable = () -> {
            Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
            startActivity(intent);
            finish();
        };

        // CHECK IF PLAYER SAVE FILE EXISTS, IF NOT, IT CREATES IT
        Utils.checkSaveFileExistence(this);

        // TEST: COMMENT THIS LINE WHEN NOT IN TESTING PHASES
//        Utils.resetSaveFileContent(this);


        Player p = PlayerManager.getInstance(this);

        // TODO: COMMENT THIS LINES WHEN NOT IN TESTING PHASES (UNLOCKS ALL CARDS AND GIVES LOTS OF COINS)
//        Log.d(TAG, "TEST: UNLOCKING ALL CARDS");
//        Integer[] allCardIDs = {
//                1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009,
//                2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009,
//                3000, 3001, 3002, 3003, 3004, 3005, 3006, 3007, 3008, 3009,
//                4000, 4001, 4002, 4003, 4004, 4005, 4006, 4007, 4008, 4009,
//                5000, 5001, 5002, 5003, 5004, 5005, 5006, 5007, 5008, 5009,
//                6000, 6001, 6002, 6003, 6004, 6005, 6006, 6007, 6008, 6009,
//                7000, 7001, 7002, 7003, 7004, 7005, 7006, 7007, 7008, 7009
//        };
//        p.setUnlocked_cards(allCardIDs);
//        Log.d(TAG, "TEST: GIVING THE PLAYER A BUNCH OF COINS");
//        p.setTower_coins(99999);
//        Player.EmotionCoins allCoins = new Player.EmotionCoins();
//        allCoins.setCoin("anger", 99999);
//        allCoins.setCoin("disgust", 99999);
//        allCoins.setCoin("fear", 99999);
//        allCoins.setCoin("happiness", 99999);
//        allCoins.setCoin("sadness", 99999);
//        allCoins.setCoin("surprise", 99999);
//        p.setEmotion_coins(allCoins);
        // -------------- END OF CHEATING, MAX-COMPLETING CODE

        PlayerManager.savePlayerData(this, p);

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            PlayerManager.saveToRemoteFromLocal(getBaseContext(), user);
        }

        // Delay the transition to the home screen
        int delay = getResources().getInteger(R.integer.splashScreen_CardsAnimationDuration) - 500;
        handler.postDelayed(runnable, delay);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("MainActivity-onStart", "currentUser: " + currentUser);
        Utils.initiateFirebaseLoginSequence(this, currentUser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        fullScreenHelper.onWindowFocusChanged(hasFocus);
    }
}