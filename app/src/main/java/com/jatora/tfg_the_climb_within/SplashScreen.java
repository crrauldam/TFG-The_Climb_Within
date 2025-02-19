package com.jatora.tfg_the_climb_within;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    private Handler handler= new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        // Get reference to the Lottie view
        LottieAnimationView lottieView = findViewById(R.id.animated_loading);

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

        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
                startActivity(intent);
                finish();
            }
        };

        // CHECK IF PLAYER SAVE FILE EXISTS, IF NOT, IT CREATES IT
        Utils.checkSaveFileExistence(this);

        // TODO: COMMENT THIS LINE WHEN NOT IN TESTING PHASES
        Utils.resetSaveFileContent(this);

        PlayerManager.getInstance(this);

        handler.postDelayed(runnable, 3500);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}