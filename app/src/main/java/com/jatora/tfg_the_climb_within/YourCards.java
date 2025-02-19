package com.jatora.tfg_the_climb_within;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YourCards extends AppCompatActivity {
    LinearLayout linearLayout;

    ImageButton basicTab;
    ImageButton fearTab;
    ImageButton angerTab;
    ImageButton disgustTab;
    ImageButton sadnessTab;
    ImageButton happinessTab;
    ImageButton surpriseTab;
    static TextView youDontHaveCards;

    String searchedDeck = "basic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_your_cards);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        gridLayout = findViewById(R.id.gridLayout);
        linearLayout = findViewById(R.id.linearLayout);

        basicTab = findViewById(R.id.basicTab);
        fearTab = findViewById(R.id.fearTab);
        angerTab = findViewById(R.id.angerTab);
        disgustTab = findViewById(R.id.disgustTab);
        sadnessTab = findViewById(R.id.sadnessTab);
        happinessTab = findViewById(R.id.happinessTab);
        surpriseTab = findViewById(R.id.surpriseTab);
        youDontHaveCards = findViewById(R.id.youDontHaveCards);

        ArrayList<Card> cards = Utils.getCardsData(this);
        ArrayList<Deck> decks = Utils.getDecksData(this);

        Player player = PlayerManager.getInstance(this);

        // player unlocked cards (ONLY IDs)
        ArrayList<Integer> pucs = new ArrayList<>(Arrays.asList(player.getUnlocked_cards()));
        // player unlocked cards (objects)
        List<Card> playerUnlockedCards = new ArrayList<>(); // Temporary batch storage

        // loop through all cards and add to the list those that the player owes
        for (Card c : cards) {
            if (pucs.contains(c.getId())) { // Check if item exists in the reference list
                playerUnlockedCards.add(c);
                Log.d("YourCards-onCreate", "card added: "+c);
            }
        }

        // draw all cards (tab is basic by default)
        drawCards(this, searchedDeck, decks, playerUnlockedCards, linearLayout);

        // set listener for every tab button, each changing the searchedDeck and updating the layout
        // by drawing the corresponding cards
        basicTab.setOnClickListener(v -> {
            searchedDeck = "basic";
            drawCards(this, searchedDeck, decks, playerUnlockedCards, linearLayout);
        });
        fearTab.setOnClickListener(v -> {
            searchedDeck = "fear";
            drawCards(this, searchedDeck, decks, playerUnlockedCards, linearLayout);
        });
        angerTab.setOnClickListener(v -> {
            searchedDeck = "anger";
            drawCards(this, searchedDeck, decks, playerUnlockedCards, linearLayout);
        });
        sadnessTab.setOnClickListener(v -> {
            searchedDeck = "sadness";
            drawCards(this, searchedDeck, decks, playerUnlockedCards, linearLayout);
        });
        disgustTab.setOnClickListener(v -> {
            searchedDeck = "disgust";
            drawCards(this, searchedDeck, decks, playerUnlockedCards, linearLayout);
        });
        happinessTab.setOnClickListener(v -> {
            searchedDeck = "happiness";
            drawCards(this, searchedDeck, decks, playerUnlockedCards, linearLayout);
        });
        surpriseTab.setOnClickListener(v -> {
            searchedDeck = "surprise";
            drawCards(this, searchedDeck, decks, playerUnlockedCards, linearLayout);
        });
    }

    /**
     * Loops through the player cards and draws those who belong to the `searchedDeck` deck. If no
     * card was drawn proceed to show a message indicating so.
     * @param context
     * @param searchedDeck
     * @param decks
     * @param playerUnlockedCards
     * @param targetLayout
     */
    public static void drawCards(Context context, String searchedDeck, ArrayList<Deck> decks, List<Card> playerUnlockedCards, LinearLayout targetLayout) {
        // clear layout so that there are no accumulated cards from previous tab selections
        targetLayout.removeAllViews();
        // set the "you dont have cards for this deck yet!" message invisible by default
        youDontHaveCards.setVisibility(View.INVISIBLE);
        // how many cards have been drawn
        int drawedCards = 0;

        // loop through player cards
        for (int i = 0; i < playerUnlockedCards.size(); i++) {
            // new line for layout (3 cards per line)
            LinearLayout newlinearLayout = new LinearLayout(context);
            newlinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            /// will store the cards that'll be drawn in the layout (max 3)
            ArrayList<Card> tempBatch = new ArrayList<>();

            // gets the deck according to the tab we're in right now
            Deck targetDeck = new Deck();
            for (Deck d : decks) {
                if (d.getName().equals(searchedDeck)) {
                    targetDeck = d;
                }
            }

            // loop cards so they are added 3 by 3 in the layout (also control out of bounds)
            while (tempBatch.size() != 3 && i < playerUnlockedCards.size()) {
                // loop through the cards of the deck's selected tab
                for (int j = 0; j < targetDeck.getCards().length; j++) {
                    // check if the actual card (i) is stored in the target deck
                    // if out of bounds then stop checking so the while loop ends too
                    try {
                        if (playerUnlockedCards.get(i).getId() == targetDeck.getCards()[j]) {
                            // if so, add it to the batch
                            tempBatch.add(playerUnlockedCards.get(i));
                            // increase counter of how many cards have been drawn
                            drawedCards++;
                            break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }
                // dont increase index to prevent double increase (from within this loop and the upper for loop)
                if (tempBatch.size() != 3) {
                    i++;
                }
            }

            // for every card in the tempbatch we add their according layout representation to the
            // linearlayout for UI showing
            for (Card c : tempBatch) {
                ConstraintLayout cardLayout = getCardLayout(context, targetDeck.getName(), c);
                newlinearLayout.addView(cardLayout);
            }

            // add line to the whole layout for effective drawing of the components
            targetLayout.addView(newlinearLayout);
        }

        // if no cards was shown (= no cards for that deck)
        if (drawedCards == 0) {
            // show message
            youDontHaveCards.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Inflates a card layout with the info of the Card object passed as parameter
     * @param context
     * @param searchedDeck
     * @param c
     * @return The inflated layout.
     */
    public static ConstraintLayout getCardLayout(Context context, String searchedDeck, Card c) {
        Log.d("YourCards-onCreate", "Player card: "+c.getId());

        // inflate card template
        ConstraintLayout cardLayout = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.card, null);

        // set name
        TextView cardName = cardLayout.findViewById(R.id.cardName);
        cardName.setText(c.getName());

        // set icon
        ImageView cardIcon = cardLayout.findViewById(R.id.cardIcon);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(c.getIcon()));

            Log.d("YourCards-onCreate", "bitmap state: " + bitmap);
            Log.d("YourCards-onCreate", "image container state: " + cardIcon);
            cardIcon.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e("YourCards-onCreate", "Error while getting bitmap image from assets: " + e);
        }

        // set image frame (for deck-related background color)
        ConstraintLayout cardImageFrame = cardLayout.findViewById(R.id.cardImageFrame);
        switch (searchedDeck) {
            case "basic":
                cardImageFrame.setBackground(AppCompatResources.getDrawable(context, R.drawable.card_img_basic));
                break;
            case "anger":
                cardImageFrame.setBackground(AppCompatResources.getDrawable(context, R.drawable.card_img_anger));
                break;
            case "disgust":
                cardImageFrame.setBackground(AppCompatResources.getDrawable(context, R.drawable.card_img_disgust));
                break;
            case "fear":
                cardImageFrame.setBackground(AppCompatResources.getDrawable(context, R.drawable.card_img_fear));
                break;
            case "happiness":
                cardImageFrame.setBackground(AppCompatResources.getDrawable(context, R.drawable.card_img_happiness));
                break;
            case "sadness":
                cardImageFrame.setBackground(AppCompatResources.getDrawable(context, R.drawable.card_img_sadness));
                break;
            case "surprise":
                cardImageFrame.setBackground(AppCompatResources.getDrawable(context, R.drawable.card_img_surprise));
                break;
        }

        return cardLayout;
    }
}