package com.jatora.tfg_the_climb_within;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class TowerSelection extends AppCompatActivity {
    private Player player;

    private int actualFragment = 0;

//    ImageButton leftArrow, rightArrow;

    ExtendedFloatingActionButton playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tower_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        leftArrow = findViewById(R.id.leftArrow);
//        rightArrow = findViewById(R.id.rightArrow);

        final String TAG = "TowerSelection-onCreate";

        playButton = findViewById(R.id.playButton);

        // VIEWPAGER FUNCTIONALITY (CARROUSEL-LIKE FRAGMENT DISPLAY)
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        WormDotsIndicator dotsIndicator = findViewById(R.id.dotsIndicator);

        // creation of adapter
        SelectionAdapter selectionAdapter = new SelectionAdapter(this);
        viewPager.setAdapter(selectionAdapter);


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Si está en los dos primeros dots
                if (position == 0 || position == 1) {
                    dotsIndicator.setDotIndicatorColor(getResources().getColor(R.color.gunmetal));
                    dotsIndicator.setStrokeDotsIndicatorColor(getResources().getColor(R.color.gunmetal));
                } else {
                    dotsIndicator.setDotIndicatorColor(getResources().getColor(R.color.light_blue));
                    dotsIndicator.setStrokeDotsIndicatorColor(getResources().getColor(R.color.light_blue));
                }

                dotsIndicator.refreshDots();  // Actualiza el estado de los puntos
            }
        });

        dotsIndicator.attachTo(viewPager);

        // TODO: DELETE ARROWS WHEN BREADCRUMB DISPLAYED
        // ARROW NAVIGATION FUNCTIONALITY
        // go back
//        leftArrow.setOnClickListener(v -> {
//            if (actualFragment > 0) {
//                actualFragment--;
//                viewPager.setCurrentItem(actualFragment);
//            }
//        });
//
//        // go next
//        rightArrow.setOnClickListener(v -> {
//            if (actualFragment < TOTAL_FRAGMENTS) {
//                actualFragment++;
//                viewPager.setCurrentItem(actualFragment);
//            }
//        });

        player = PlayerManager.getInstance(this);

        playButton.setOnClickListener(v -> {
            Log.d(TAG, "Play button clicked");
            // unable play button to prevent multiple transitions on repeated clicking
            playButton.setClickable(false);

            int[] unlockedTowers = player.getUnlocked_towers();
            boolean towerIsUnlocked = false;

            // TODO: THIS IS JUST FOR TESTING, UNCOMMENT BLOCK BELOW WHEN WANTING TO CHECK IF TOWER IS UNLOCKED
//            Intent intent = new Intent(this, BattleScreen.class);
//            intent.putExtra("towerID", viewPager.getCurrentItem());
//            Utils.changeActivity(intent, this, BattleScreen.class, R.anim.slide_out_left, R.anim.slide_in_right);


            // TODO: UNCOMMENT FOR REAL USE
            // check if selected tower is unlocked
            for (int towerID : unlockedTowers) {
                // if unlocked then start game in that tower
                if (towerID == viewPager.getCurrentItem()) {
                    Intent intent = new Intent(this, BattleScreen.class);
                    intent.putExtra("towerID", viewPager.getCurrentItem());
                    Utils.changeActivity(intent, this, R.anim.slide_out_left, R.anim.slide_in_right);
                    towerIsUnlocked = true;
                }
            }

            Runnable setPlayButtonClickableAgain = new Runnable() {
                @Override
                public void run() {
                    playButton.setClickable(true);
                }
            };

            // show message if not unlocked
            if (!towerIsUnlocked) {
                Log.d(TAG, "Tower not unlocked, showing message.");
                Toast t = Toast.makeText(this, getResources().getString(R.string.tower_not_unlocked), Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();

                // set the button clickable again after a 2s delay to prevent spam clicking while
                // showing toast message
                playButton.postDelayed(setPlayButtonClickableAgain, 2000);
            } else {
                // set the button clickable again after a 1s delay (not important for the user
                // since this will happen while changing activity) for future use of the button
                playButton.postDelayed(setPlayButtonClickableAgain, 1000);
            }
        });
    }
}