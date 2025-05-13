package com.jatora.tfg_the_climb_within;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.content.res.ResourcesCompat;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shop extends AppCompatActivity {

    private FullScreenHelper fullScreenHelper;

    LinearLayout linearLayout;
    ImageView coinImg;
    TextView totalEmotionCoins;

    Button fearTab;
    Button angerTab;
    Button disgustTab;
    Button sadnessTab;
    Button happinessTab;
    Button surpriseTab;
    static TextView noCardsToBeBought;
    static TextView youHaveAllCards;
    private FirebaseAuth mAuth;

    // stores the last shop item selected
    static int prevItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "Shop-onCreate";

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);

        fullScreenHelper = new FullScreenHelper(this);
        fullScreenHelper.enableFullScreen();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();




        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null) {
                    PlayerManager.saveToRemoteFromLocal(getBaseContext(), user);
                }
                finish();
            }
        });

        linearLayout = findViewById(R.id.linearLayout);

        coinImg = findViewById(R.id.coinImg);
        totalEmotionCoins = findViewById(R.id.totalEmotionCoins);

        fearTab = findViewById(R.id.fearTab);
        angerTab = findViewById(R.id.angerTab);
        disgustTab = findViewById(R.id.disgustTab);
        sadnessTab = findViewById(R.id.sadnessTab);
        happinessTab = findViewById(R.id.happinessTab);
        surpriseTab = findViewById(R.id.surpriseTab);
        noCardsToBeBought = findViewById(R.id.noCardsToBeBought);

        ArrayList<Card> cards = Utils.getCardsData(this);
        ArrayList<Deck> decks = Utils.getDecksData(this);

        Player player = PlayerManager.getInstance(this);



        // player unlocked cards (ONLY IDs)
        ArrayList<Integer> pucs = new ArrayList<>(Arrays.asList(player.getUnlocked_cards()));
        // player unlocked cards (objects)
        List<Card> playerNonUnlockedCards = new ArrayList<>(); // Temporary batch storage

        // loop through all cards and add to the list those that the player DOESNT owe
        for (Card c : cards) {
            if (!pucs.contains(c.getId())) { // Check if item DOESNT exist in the reference list
                playerNonUnlockedCards.add(c);
                Log.d(TAG, "card added: "+c);
            }
        }

        Log.d(TAG, "player state: "+player);
        // draw all cards (tab is basic by default)
        drawCards(this, player, "fear", decks, playerNonUnlockedCards, linearLayout, coinImg, totalEmotionCoins, pucs);

        // set listener for every tab button, each changing the searchedDeck and updating the layout
        // by drawing the corresponding cards

        fearTab.setOnClickListener(v -> {
            drawCards(this, player, "fear", decks, playerNonUnlockedCards, linearLayout, coinImg, totalEmotionCoins, pucs);
        });
        angerTab.setOnClickListener(v -> {
            drawCards(this, player, "anger", decks, playerNonUnlockedCards, linearLayout, coinImg, totalEmotionCoins, pucs);
        });
        sadnessTab.setOnClickListener(v -> {
            drawCards(this, player, "sadness", decks, playerNonUnlockedCards, linearLayout, coinImg, totalEmotionCoins, pucs);
        });
        disgustTab.setOnClickListener(v -> {
            drawCards(this, player, "disgust", decks, playerNonUnlockedCards, linearLayout, coinImg, totalEmotionCoins, pucs);
        });
        happinessTab.setOnClickListener(v -> {
            drawCards(this, player, "happiness", decks, playerNonUnlockedCards, linearLayout, coinImg, totalEmotionCoins, pucs);
        });
        surpriseTab.setOnClickListener(v -> {
            drawCards(this, player, "surprise", decks, playerNonUnlockedCards, linearLayout, coinImg, totalEmotionCoins, pucs);
        });
    }


    /**
     * Loops through the player cards and draws those who belong to the `searchedDeck` deck. If no
     * card was drawn proceed to show a message indicating so.
     * @param context
     * @param searchedDeck
     * @param decks
     * @param playerNonUnlockedCards
     * @param targetLayout
     */
    public void drawCards(Context context, Player player, String searchedDeck, ArrayList<Deck> decks, List<Card> playerNonUnlockedCards, LinearLayout targetLayout, ImageView coinImg, TextView totalEmotionCoins, ArrayList<Integer> pucs) {
        final String TAG = "Shop-drawCards";

        // clear layout so that there are no accumulated cards from previous tab selections
        targetLayout.removeAllViews();
        // set the "you dont have cards for this deck yet!" message invisible by default
        noCardsToBeBought.setVisibility(View.INVISIBLE);

        // gets the deck according to the tab we're in right now
        Deck targetDeck = new Deck();
        for (Deck d : decks) {
            if (d.getName().equals(searchedDeck)) {
                targetDeck = d;
            }
        }

        // set coins for that deck in the upper label
        int coins = player.getEmotion_coins().getCoin(targetDeck.getName());

        totalEmotionCoins.setText(String.valueOf(coins));
        Log.d(TAG, "Player emotion coins for " + targetDeck.getName() + ": " + coins + " coins");

        // set top label coin icon
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(targetDeck.getCoin()));

            Log.d(TAG, "bitmap state: " + bitmap);
            Log.d(TAG, "image container state: " + coinImg);
            // set coin drawable for upper label too since they all use the same bitmap
            coinImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error while getting bitmap image from assets: " + e);
        }

        // how many cards have been drawn
        int drawedCards = 0;

        // loop through player cards
        for (int i = 0; i < playerNonUnlockedCards.size(); i++) {
            // new line for layout (3 cards per line)
            LinearLayout newlinearLayout = new LinearLayout(context);
            newlinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            /// will store the cards that'll be drawn in the layout (max 3)
            ArrayList<Card> tempBatch = new ArrayList<>();

            // loop cards so they are added 3 by 3 in the layout (also control out of bounds)
            while (tempBatch.size() != 3 && i < playerNonUnlockedCards.size()) {
                // loop through the cards of the deck's selected tab
                for (int j = 0; j < targetDeck.getCards().length; j++) {
                    // check if the actual card (i) is stored in the target deck
                    // if out of bounds then stop checking so the while loop ends too
                    try {
                        if (playerNonUnlockedCards.get(i).getId() == targetDeck.getCards()[j]) {
                            // if so, add it to the batch
                            tempBatch.add(playerNonUnlockedCards.get(i));
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
                LinearLayout shopItemLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.card_shop, null);
                ConstraintLayout card = shopItemLayout.findViewById(R.id.card);
                // add corresponding info to the card
                getCardLayout(context, searchedDeck, c, card);

                ImageView cardCoinImg = shopItemLayout.findViewById(R.id.cardCoinImg);
                // set coin image
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(targetDeck.getCoin()));

                    Log.d(TAG, "bitmap state: " + bitmap);
                    Log.d(TAG, "image container state: " + cardCoinImg);
                    cardCoinImg.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Log.e(TAG, "Error while getting bitmap image from assets: " + e);
                }

                // set price to the card price label
                TextView cardPrice = shopItemLayout.findViewById(R.id.cardPrice);
                cardPrice.setText(String.valueOf(c.getUnlock_cost()));

                // add click listener to card for it to be able to be bought
                addClickListenerToShopItemLayoutTEST(context, player, searchedDeck, shopItemLayout, c, pucs, playerNonUnlockedCards, targetDeck);

                // add card to layout
                newlinearLayout.addView(shopItemLayout);
            }

            // add line to the whole layout for effective drawing of the components
            targetLayout.addView(newlinearLayout);
        }

        // if no cards was shown (= no cards for that deck)
        if (drawedCards == 0) {
            // show message
            noCardsToBeBought.setVisibility(View.VISIBLE);
        }
    }

    // DONE: TEST TO IMPLEMENT CONFIRMATION WHEN BUYING
    /**
     * Adds a click listener to the LinearLayout passed as argument so that it is able to detect
     * the intention of buying it.
     * @param context
     * @param player
     * @param searchedDeck
     * @param shopItemLayout
     * @param c
     */
    private void addClickListenerToShopItemLayoutTEST(Context context, Player player, String searchedDeck, LinearLayout shopItemLayout, Card c, ArrayList<Integer> pucs, List<Card> playerNonUnlockedCards, Deck targetDeck) {
        final String TAG = "Shop-addClickListenerToShopItemLayout";

        shopItemLayout.setOnClickListener(v -> {
            Log.d(TAG, "card pressed: "+c.getId());

            showConfirmationDialog(context, player, searchedDeck, shopItemLayout, c, pucs, playerNonUnlockedCards, targetDeck);
        });
    }

    public void showConfirmationDialog(Context context, Player player, String searchedDeck, LinearLayout shopItemLayout, Card c, ArrayList<Integer> pucs, List<Card> playerNonUnlockedCards, Deck targetDeck) {
        final String TAG = "Shop-showConfirmationDialog";

        Log.d(TAG, "Showing confirm buy dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View menu = getLayoutInflater().inflate(R.layout.shop_confirm_buy_dialog, null);

        // set tower coin image
        ImageView coinImg = menu.findViewById(R.id.coinImg);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open(targetDeck.getCoin()));
            coinImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error while getting bitmap image from assets: " + e);
        }

        TextView amount = menu.findViewById(R.id.confirmAmount);
        Log.d(TAG, "card cost: "+c.getUnlock_cost());
        amount.setText(String.valueOf(c.getUnlock_cost()));

        // open pop up menu
        builder.setView(menu);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.battle_screen_menu_bg, null));
        dialog.show();

        ExtendedFloatingActionButton yes = menu.findViewById(R.id.yes);
        yes.setOnClickListener(v -> {
            // DONE: CHECK IF PLAYER HAS ENOUGH COINS TO BUY THAT CARD
            if (player.getEmotion_coins().getCoin(searchedDeck) >= c.getUnlock_cost()) {
                // update player unlocked cards
                // add the card's ID to the player's collection
                pucs.add(c.getId());
//                        Integer[] pucs = player.getUnlocked_cards();
//                        pucs = Arrays.copyOf(pucs, pucs.length+1);
//                        pucs[pucs.length-1] = c.getId();
                player.setUnlocked_cards(pucs.toArray(new Integer[0]));

                // update player emotion coins for that specific deck
                Player.EmotionCoins newEmotionCoins = player.getEmotion_coins();
                newEmotionCoins.setCoin(searchedDeck, newEmotionCoins.getCoin(searchedDeck)-c.getUnlock_cost());
                player.setEmotion_coins(newEmotionCoins);

                // update the non-unlocked cards list and remove
                playerNonUnlockedCards.remove(c);

                // DONE: AFTER ADDING NEW CARD, UPDATE SAVE.JSON FILE (MAKE METHOD IN UTILS CLASS)
                Log.d(TAG, "saving player data");
                PlayerManager.savePlayerData(v.getContext(), player);
//                            Utils.savePlayerData(context, player);
                Log.d(TAG, "successfully saved player data");

                Log.d(TAG, "player unlocked cards: "+ Arrays.toString(pucs.toArray()));

                // reset item selected
                prevItem = 0;

                // set coins for that deck in the upper label
                int coins = player.getEmotion_coins().getCoin(targetDeck.getName());
                totalEmotionCoins.setText(String.valueOf(coins));

                // show card as no available for buying
                shopItemLayout.findViewById(R.id.hideCard).setVisibility(View.VISIBLE);
                shopItemLayout.findViewById(R.id.hidePrice).setVisibility(View.VISIBLE);
                v.setClickable(false);

                dialog.cancel();
            } else {
                // if not enough coins, show toast with message indicating so
                Toast.makeText(context, R.string.not_enough_coins, Toast.LENGTH_SHORT).show();
            }
        });

        ExtendedFloatingActionButton no = menu.findViewById(R.id.no);
        no.setOnClickListener(v -> {
            dialog.cancel();
        });
    }


    /**
     * Sets the info into the given's card constraint layout
     * @param context
     * @param searchedDeck
     * @param c
     * @param cardLayout
     */
    public void getCardLayout(Context context, String searchedDeck, Card c, ConstraintLayout cardLayout) {
        final String TAG = "Shop-getCardLayout";

        Log.d(TAG, "Player card: "+c.getId());

        // set name
        TextView cardName = cardLayout.findViewById(R.id.cardName);
        cardName.setText(c.getName());

        // set icon
        ImageView cardIcon = cardLayout.findViewById(R.id.cardIcon);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(c.getIcon()));

            Log.d(TAG, "bitmap state: " + bitmap);
            Log.d(TAG, "image container state: " + cardIcon);
            cardIcon.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error while getting bitmap image from assets: " + e);
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
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        fullScreenHelper.onWindowFocusChanged(hasFocus);
    }
}