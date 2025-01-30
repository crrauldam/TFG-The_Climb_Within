package com.jatora.tfg_the_climb_within;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeScreen extends AppCompatActivity {
    private AppCompatButton continueButton;
    private AppCompatButton newGameButton;
    private AppCompatButton shopButton;
    private AppCompatButton yourCardsButton;
    private AppCompatButton settingsButton;
    private AppCompatButton faqButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        continueButton = findViewById(R.id.Continue);
        newGameButton = findViewById(R.id.newGame);
        shopButton = findViewById(R.id.shop);
        yourCardsButton = findViewById(R.id.yourCards);
        settingsButton = findViewById(R.id.settingsButton);
        faqButton = findViewById(R.id.faq);

        continueButton.setOnClickListener(v -> {
            Utils.changeActivity(this, TowerSelection.class, R.anim.slide_out_left, R.anim.slide_in_right);
        });

        newGameButton.setOnClickListener(v -> {
            Utils.changeActivity(this, TowerSelection.class, R.anim.slide_out_left, R.anim.slide_in_right);
        });

        yourCardsButton.setOnClickListener(v -> {
            Utils.changeActivity(this, Your_Cards.class, R.anim.slide_out_top, R.anim.slide_in_bottom);
        });

        shopButton.setOnClickListener(v -> {
            Utils.changeActivity(this, Shop.class, R.anim.slide_out_right, R.anim.slide_in_left);
        });

        settingsButton.setOnClickListener(v -> {
            Utils.changeActivity(this, Settings.class, R.anim.slide_out_bottom, R.anim.slide_in_top);
        });

        faqButton.setOnClickListener(v -> {
        });
    }
}