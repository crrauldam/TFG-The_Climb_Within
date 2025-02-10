package com.jatora.tfg_the_climb_within;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;

public class TowerSelection extends AppCompatActivity {
    private static final int TOTAL_FRAGMENTS = 6;

    private int actualFragment = 0;

//    ImageButton leftArrow, rightArrow;

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


    }
}