package com.jatora.tfg_the_climb_within;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BattleScreen extends AppCompatActivity {
    // firebase things
    FirebaseAuth mAuth;

    // readonly data
    ArrayList<Deck> ALL_DECKS;
    ArrayList<Card> ALL_CARDS;
    ArrayList<Enemy> ALL_ENEMIES;
    ArrayList<Tower> ALL_TOWERS;

    final int TOTAL_STAGES = 5; // total stages the player will face in the tower
    final int TOTAL_FLOORS = 3; // 3 floors
    final int STAGES_TO_REST = 2; // how many stages the player will have to fight to go to shop and rest

    // TODO: DEFINE GAME ECONOMY
    final int TOWER_COINS_PER_STAGE = 0;
    final int EMOTION_COINS_PER_STAGE = 0;

    // flag for game state [true = game still in progress | false = game over]
    boolean game = true;
    int floor = 1;
    int stage = 1;

    // entities
    Player player;
    Tower tower;

    TextView storyNarrationView;
    ConstraintLayout storyNarrationBackground;

    ImageButton menuButton;
    TextView enemyName;
    ImageView enemyImg;
    TextView narrationView;

    // card-related UI
    GridLayout playableCards;
    View blockCardClicking;
    View hoveredCard;
    TextView cardDescription;

    // HP components
    HealthBarView enemyHPBar;
    TextView enemyHP;
    HealthBarView playerHPBar;
    TextView playerHP;

    // shields
    TextView enemyShieldView;

    // for waiting response from another activity to continue processing
    private ActivityResultLauncher<Intent> activityResultLauncher;


    private final int[] enemyShield = new int[]{0};
    TextView playerShieldView;
    private final int[] playerShield = new int[]{0};

    // flag to know battle state
    boolean hasEndDialogBeenShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_battle_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        // set ActivityResultLauncher for resting after N stages
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // if callback says it was OK then proceed to next stage
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // update player object after rest
                        player = PlayerManager.getInstance(this);
                        // if rest, then means next floor
                        floor++;
                        stage++;
                        // TODO: ERASE THE FOLLOWING LINE, ONLY FOR TEST PURPOSES!!
                        // add "de estrangis" extra card of that tower for letting it complete the tower
                        // since the basic deck isnt enough
                        Integer[] deck = player.getDeck();
                        deck = Arrays.copyOf(deck, deck.length + 1);
                        deck[deck.length - 1] = 5000;
                        player.setDeck(deck);

                        // TODO: STOP PRECOCIOUS STAGE START
                        playStage(player, tower, floor, stage);
                    }
                }
        );

        // show game menu on system back button functionality to prevent the player from unintentionally leaving game
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showMenu(getBaseContext());
            }
        });

        ALL_DECKS = Utils.getDecksData(this);
        ALL_CARDS = Utils.getCardsData(this);
        ALL_ENEMIES = Utils.getEnemiesData(this);
        ALL_TOWERS = Utils.getTowersData(this);
        
        int towerID = getIntent().getIntExtra("towerID", 0);
        tower = getTowerInfo(towerID);

        // get visual components
        storyNarrationView = findViewById(R.id.storyNarrationView);
        storyNarrationBackground = findViewById(R.id.storyNarrationBackground);

        menuButton = findViewById(R.id.menuButton);
        enemyName = findViewById(R.id.enemyName);
        enemyImg = findViewById(R.id.enemyImg);
        narrationView = findViewById(R.id.narrationView);

        cardDescription = findViewById(R.id.cardDescription);
        playableCards = findViewById(R.id.playableCards);
        blockCardClicking = findViewById(R.id.blockCardClicking);

        enemyHPBar = findViewById(R.id.enemyHPBar);
        enemyHP = findViewById(R.id.enemyHP);

        playerHPBar = findViewById(R.id.playerHPBar);
        playerHP = findViewById(R.id.playerHP);

        enemyShieldView = findViewById(R.id.enemyShield);
      
        playerShieldView = findViewById(R.id.playerShield);

        startGame();
    }

    /**
     * Game structure:
     * - Floor = 1
     * - rests = 0
     * - stage = 1
     * show stage + (rests (0) * STAGES_TO_REST) = 1
     * - stage = 2
     * show stage + (rests (0) * STAGES_TO_REST) = 2
     * - REST (stage == STAGES_TO_REST)
     * - rests += 1 (1)
     * - Floor++ (2)
     * - stage = 1
     * show stage + (rests (1) * STAGES_TO_REST) = 3
     * - stage = 2
     * show stage + (rests (1) * STAGES_TO_REST) = 4
     * - REST (stage == STAGES_TO_REST)
     * - rests += 1 (2)
     * - Floor++ (3)
     * - BOSS (rests == totalRests (TOTAL_STAGES / STAGES_TO_REST))
     * - stage = 1
     * show stage + (rests (2) * STAGES_TO_REST) = 5
     */
    private void startGame() {
        final String TAG = "BattleScreen-startGame";

        Log.d(TAG, "Starting game...");
//        // TODO: DELETE WHEN NOT TESTING
//        new Handler().postDelayed(() -> {
//            Utils.playStoryNarration(this, storyNarrationBackground, storyNarrationView, tower.getName());
//        }, 3000);

        // set up player for whole game
        player = PlayerManager.getInstance(this);
        player.setDeck(getInitialDeck());

        Log.d(TAG, "Player deck: " + Arrays.toString(player.getDeck()));

        // calculates how many rests the player will have until the boss fight
        int totalRests = TOTAL_STAGES / STAGES_TO_REST;
        int rests = 0;

        floor = 1;
        stage = 1;

        // show pop up menu with enemy info and options
        int[] finalFloor = {floor};
        int[] finalStage = {stage};

        menuButton.setOnClickListener(v -> {
            showMenu(v.getContext());
        });

        playStage(player, tower, floor, stage);
    }

    /**
     * Shows the pop up menu with game info and options.
     * @param context
     */
    private void showMenu(Context context) {
        final String TAG = "BattleScreen-showMenu";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View menu = getLayoutInflater().inflate(R.layout.battle_screen_menu, null);

        // set enemy name
        TextView towerName = menu.findViewById(R.id.towerName);
        towerName.setText(tower.getName().toUpperCase());

        // set enemy image
        ImageView towerImg = menu.findViewById(R.id.towerImg);
        try {
            Log.d(TAG, "Tower img: " + tower.getImg());
            Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(tower.getImg()));
            towerImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e("BattleScreen", "Error while getting bitmap image from assets: " + e);
        }

        // set floor
        TextView floorView = menu.findViewById(R.id.floor);
        floorView.setText(getString(R.string.floor) + floor);

        // set stage
        TextView stageView = menu.findViewById(R.id.stage);
        stageView.setText(getString(R.string.stage) + (stage - (STAGES_TO_REST * (floor - 1))));

        // set total amount of tower coins
        TextView totalTowerCoins = menu.findViewById(R.id.amount);
        totalTowerCoins.setText(String.valueOf(player.getTower_coins()));

        // set tower coin image
        ImageView coinImg = menu.findViewById(R.id.coinImg);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("img/coins/tower.png"));
            coinImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error while getting bitmap image from assets: " + e);
        }

        // open pop up menu
        builder.setView(menu);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.battle_screen_menu_bg, null));
        dialog.show();

        // set return to battle functionality
        Button backtoBattleButton = menu.findViewById(R.id.backtoBattleButton);
        backtoBattleButton.setOnClickListener(v1 -> {
            Log.d(TAG, "Back to battle button clicked.");
            dialog.cancel();
        });

        // set exit battle functionality
        Button exitBattleButton = menu.findViewById(R.id.exitBattleButton);
        exitBattleButton.setOnClickListener(v1 -> {
            Log.d(TAG, "Exit battle button clicked.");
            player.restoreHP(); // heal player
            this.finish();
        });

        Button settingsButton = menu.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v1 -> {
            Log.d(TAG, "Settings button clicked.");
            settingsButton.setClickable(false);
            settingsButton.postDelayed(() -> settingsButton.setClickable(true), 1000);
            Utils.changeActivity(this, Settings.class, R.anim.slide_out_bottom, R.anim.slide_in_top);
        });
    }

    /**
     * Play a stage of the tower.
     *
     * @param player
     * @param tower
     */
    private void playStage(Player player, Tower tower, int floor, int stage) {
        final String TAG = "BattleScreen-playStage";

        Log.d(TAG, "Begins stage: " + stage + " for tower: " + tower.getName() + " floor: " + floor);

        // by default set cards clickable
        blockCardClicking.setVisibility(View.GONE);

        Enemy enemy = generateEnemy(tower, floor);

        try {
            drawScreenElements(player, enemy);
        } catch (IOException e) {
            Log.e(TAG, "Error while drawing screen elements");
            throw new RuntimeException(e);
        }

        // TODO: make battle loop

    }

    /**
     * <ul>
     *     <li>shuffles the deck and displays the cards</li>
     *     <li>sets player and enemy stats on screen</li>
     *     <li>update stage and floor displayed</li>
     * </ul>
     * @param player
     * @param enemy
     */
    private void drawScreenElements(Player player, Enemy enemy) throws IOException {
        final String TAG = "BattleScreen-drawScreenElements";

        // get color for enemy hp bar based on tower's name match with deck
        String color = "";
        // since calm deck does not exist, we have to check for it in order to set it manually
        if (tower.getDialogues().equalsIgnoreCase("calm")) {
            int colorResId = R.color.calm;
            int colorInt = ContextCompat.getColor(this, colorResId); // get int color value
            color = String.format("#%08X", (colorInt)); // extract hex string from it
        } else {
            for (Deck d : ALL_DECKS) {
                Log.d(TAG, d.getName());
                if (d.getName().equalsIgnoreCase(tower.getDialogues())) {
                    color = d.getColor();
                }
            }
        }

        Log.d(TAG, "Drawing player elements");
        playerHPBar.setMaxHP(player.getMaxhp());
        playerHPBar.setHP(player.getHp());
        playerHP.setText(player.getHp() + "/" + player.getMaxhp());
        playerShieldView.setText(String.valueOf(playerShield[0]));
        displayPlayerDeck(player, enemy);

        Log.d(TAG, "Drawing enemy elements");
        enemyHPBar.setHealthBarColor(Color.parseColor(color));
        enemyHPBar.setMaxHP(enemy.getMaxhp());
        enemyHPBar.setHP(enemy.getHp());
        enemyHP.setText(enemy.getHp() + "/" + enemy.getMaxhp());
        enemyName.setText(enemy.getName());
        enemyShieldView.setText(String.valueOf(enemyShield[0]));
        // set enemy image
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(this.getAssets().open(enemy.getImg()));

            Log.d(TAG, "bitmap state: " + bitmap);
            Log.d(TAG, "image container state: " + enemyImg);
        } catch (IOException e) {
            Log.e(TAG, "Error while getting bitmap image from assets: " + e);
            Log.e(TAG, "Trying again with default image");
            bitmap = BitmapFactory.decodeStream(this.getAssets().open("img/enemies/enemie.png"));
        }
        enemyImg.setImageBitmap(bitmap);

        enemyImg.setVisibility(View.VISIBLE);
        enemyImg.setAlpha(1f);
    }

    // TODO: ADD FUNCTIONALITY TO CARDS FOR THEM TO BE PLAYED
    public void displayPlayerDeck(Player player, Enemy enemy) {
        final String TAG = "BattleScreen-displayPlayerDeck";

        Log.d(TAG, "Displaying player deck");

        // clean deck layout
        playableCards.removeAllViews();
        Integer[] deck = player.getDeck();

        // stores the Card objects that match the id's the player has in his deck
        List<Card> playerDeck = ALL_CARDS.stream()
                .filter(card -> Arrays.asList(deck).contains(card.getId()))
                .collect(Collectors.toList());

        // shuffle the deck
        Collections.shuffle(playerDeck);

        // add functionality to each card
        for (Card c : playerDeck) {
            Log.d(TAG, "Drawing card: " + c.getId());

            String cardDeck = "basic";
            for (Deck d : ALL_DECKS) {
                if (Arrays.stream(d.getCards()).anyMatch(id -> id == c.getId())) {
                    cardDeck = d.getName();
                }
            }

            ConstraintLayout card = getCardLayout(this, cardDeck, c);

            card.setOnClickListener(v -> {
                cardDescription.setText(c.getDescription());
                cardDescription.setVisibility(View.VISIBLE);

                // if it is not the first pressed card, the last one returns to its
                // initial position to let the player focus only on one card at a time
                if (hoveredCard != null) {
                    if (hoveredCard == card) {
                        Toast.makeText(this, "You played: " + c.getId(), Toast.LENGTH_SHORT).show();
//                        hoveredCard.setVisibility(View.GONE);
//                        cardDescription.setVisibility(View.INVISIBLE);
//                        cardDescription.setText("");
//                        playableCards.removeView(card);

                        // temporarily disable cards layout so they cannot be clicked while playing other card
                        Log.d(TAG, "locking player cards");
                        blockCardClicking.setVisibility(View.VISIBLE);
                        switch (playCard(c, card, player, enemy, playerShield, playerShieldView, enemyShield, enemyShieldView, playerHP, playerHPBar, enemyHP, enemyHPBar, new Callback_TCW() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "unlocking cards to be played");
                                blockCardClicking.setVisibility(View.GONE);
                            }

                            @Override
                            public void onSuccess(Context context) {

                            }

                            @Override
                            public void onFailure(String errorMessage) {

                            }
                        })) {
                            case -1: // card has not been played
                                card.animate().
                                        translationY(0)
                                        .setDuration(200)
                                        .start();
                                break;
                            case 0: // card has been played
                                cardDescription.setVisibility(View.GONE);

                                // after player's action, enemy attacks with a certain delay
                                new Handler().postDelayed(() -> {
                                    if (!checkWinner(player, enemy)) { // check if in player's turn someone won
                                        // if there's no winner then the enemy attacks
                                        attack(player, enemy.getAtk(), playerShield, playerShieldView, playerHP, playerHPBar, new Callback_TCW() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d(TAG, "unlocking cards to be played");
                                                blockCardClicking.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onSuccess(Context context) {

                                            }

                                            @Override
                                            public void onFailure(String errorMessage) {

                                            }
                                        });
                                        checkWinner(player, enemy); // check if in enemy's turn someone won
                                    }

                                }, 1100);
                                break;
                            case 1: // card with FINAL state has been played
                                if (!checkWinner(player, enemy)) { // check if in player's turn someone won
                                    showEndGameDialog("YOU LOST");
                                }
                                break;
                        }

                    }
                    hoveredCard.animate().
                            translationY(0)
                            .setDuration(200)
                            .start();
                }

                hoveredCard = v;

                // makes a float animation of 100px above (card goes up when touched)
                v.animate()
                        .translationY(-100)
                        .setDuration(200)
                        .start();

            });

            playableCards.addView(card);
        }
    }

    /**
     * Inflates a card layout with the info of the Card object passed as parameter
     *
     * @param context
     * @param searchedDeck
     * @param c
     * @return The inflated layout.
     */
    public static ConstraintLayout getCardLayout(Context context, String searchedDeck, Card c) {
        final String TAG = "BattleScreen-getCardLayout";
        Log.d(TAG, "Player card: " + c.getId());

        // inflate card template
        ConstraintLayout cardLayout = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.card, null);

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

        return cardLayout;
    }

    /**
     * @param towerID
     * @return the tower with the same id as the one searched.
     */
    private Tower getTowerInfo(int towerID) {
        Tower tower = null;
        // load all towers
//        ArrayList<Tower> towers = Utils.getTowersData(this);
        // search tower with same id
        for (Tower t : ALL_TOWERS) {
            if (t.getId() == towerID) {
                tower = t;
                break;
            }
        }

        return tower;
    }

    /**
     * Generates a random enemy for the player to face based on the tower and floor its in.
     *
     * @param tower
     * @param floor
     * @return The Enemy object representing the chosen enemy.
     */
    private Enemy generateEnemy(Tower tower, int floor) {
        final String TAG = "BattleScreen-generateEnemy";

        Enemy enemy = null;

        Log.d(TAG, "Generating random enemy for tower: " + tower.getName() + " floor: " + floor);
        int[] floorEnemies = tower.getEnemies().getFloor(floor);

        //
        int enemyID = floorEnemies[(int) (Math.random() * floorEnemies.length)];
        float towerMultiplier = tower.getMultiplier();

//        ArrayList<Enemy> enemies = Utils.getEnemiesData(this);

        // search enemy with same id
        for (Enemy e : ALL_ENEMIES) {
            if (e.getId() == enemyID) {
                enemy = e.clone();
                // update enemy stats based on tower multiplier
                int enemyhp = enemy.getHp();
                int enemymaxhp = enemy.getMaxhp();
                int enemyatk = enemy.getAtk();
              
                Log.d(TAG, "tower multiplier: " + towerMultiplier);
                Log.d(TAG, "enemy hp: " + enemyhp + " enemy maxhp: " + enemymaxhp + " enemy atk: " + enemyatk);
                enemy.setMaxhp((int) (enemymaxhp * towerMultiplier));
                enemy.setHp((int) (enemyhp * towerMultiplier));
                enemy.setAtk((int) (enemyatk * towerMultiplier));
                Log.d(TAG, "enemy new hp: " + enemy.getHp() + " enemy new maxhp: " + enemy.getMaxhp() + " enemy new atk: " + enemy.getAtk());
//                enemy.setMaxhp((int) (enemy.getMaxhp() * tower.getMultiplier()));
//                enemy.setHp((int) (enemy.getHp() * tower.getMultiplier()));
//                enemy.setAtk((int) (enemy.getAtk() * tower.getMultiplier()));
                break;
            }
        }

        Log.d(TAG, "Generated enemy id: " + enemy.getId() + " name: " + enemy.getName());
        return enemy;
    }

    /**
     * @return An array with the initial deck for the player when starting a new game.
     */
    private Integer[] getInitialDeck() {
        final String TAG = "BattleScreen-getInitialDeck";

        Integer[] initialDeck = new Integer[0];

        for (Deck d : ALL_DECKS) {
            if (d.getName().equalsIgnoreCase("basic")) {
                Log.d(TAG, "Checking basic deck");
                for (int cardID : d.getCards()) {
                    Log.d(TAG, "Adding card: (" + cardID + ") to initial deck");
                    initialDeck = Arrays.copyOf(initialDeck, initialDeck.length + 1);
                    initialDeck[initialDeck.length - 1] = cardID;
                }
            }
        }

        return initialDeck;
    }

    /**
     * Realizes the action described on the selected card.
     *
     * @param c
     * @param hoveredCard
     * @param player
     * @param enemy
     * @param playerShield
     * @param playerShieldView
     * @param enemyShield
     * @param enemyShieldView
     * @param playerHP
     * @param playerHPBar
     * @param enemyHP
     * @param enemyHPBar
     * @return -1 = card not played | 0 = card played | 1 = card played and end battle
     */
    private int playCard(Card c, View hoveredCard, Player player, Enemy enemy, int[] playerShield, TextView playerShieldView, int[] enemyShield, TextView enemyShieldView, TextView playerHP, HealthBarView playerHPBar, TextView enemyHP, HealthBarView enemyHPBar, Callback_TCW callback) {
        final String TAG = "BattleScreen-playCard";
        Log.d(TAG, "Card played: " + c.getName());
        int playResult = 0;

        switch (c.getType()) {
            case ATTACK:
                attack(enemy, c.getEffect(), enemyShield, enemyShieldView, enemyHP, enemyHPBar, callback);
                break;
            case HEAL:
                if (player.getHp() == player.getMaxhp()) {
                    showBattleNarration(getString(R.string.hp_is_full));
                    playResult = -1;
                } else {
                    heal(player, c.getEffect(), playerHP, playerHPBar, callback);
                }
                break;
            case SHIELD:
                increaseShield(playerShield, c.getEffect(), playerShieldView);
                break;
            case ABSORB: // damage and heal same amount
                absorb(c, player, enemy, enemyShield, enemyShieldView, playerHP, playerHPBar, enemyHP, enemyHPBar, callback);
                break;
        }

        if (playResult != -1) {
            // remove card from visual ONLY IF PLAYED
            hoveredCard.setVisibility(View.GONE);
            cardDescription.setVisibility(View.INVISIBLE);
            cardDescription.setText("");
            playableCards.removeView(hoveredCard);

            // TODO: remove played card after being played from player's deck, restore when rest
//            ArrayList<Integer> playerDeck = new ArrayList<>(Arrays.asList(player.getDeck()));
//            playerDeck.remove(c.getId());
//            player.setDeck(playerDeck.toArray(new Integer[0]));
        } else {
            // if card not played, set cards clickable again by calling the callback onSucc ess() method
            callback.onSuccess();
        }

        return playResult;
    }

    /**
     * A certain entity gets damaged by an incoming attack.
     * Uses a literal value for the effect 'cause it can be used too for the enemy.
     *
     * @param target
     * @param dmg
     * @param targetShield
     */
    private void attack(Entity target, int dmg, int[] targetShield, TextView targetShieldView, TextView targetHP, HealthBarView targetHPBar, Callback_TCW callback) {
        final String TAG = "BattleScreen-attack";
        Log.d(TAG, "Attacking to target: " + target.getName());

        // TODO: PLAY ENEMY ATTACK ANIMATION
        if (target instanceof Player) {
            playEnemyAttackAnimation();
        }

        if (targetShield[0] > 0) {
            // control overflowing damage that the shield can't cover
            dmg -= targetShield[0];

            // if even after taking into account the shield amount there is damage to be done
            if (dmg > 0) {
                targetShield[0] = 0; // means shield has no more amount
//                playSFX("break_shield");
                target.setHp(target.getHp() - dmg); // and the target gets damaged
//                playSFX("take_damage");
            } else {
                // if after the attack there is no more damage to be done, it means
                // the shield remains some points, so we update them
                if (dmg == 0) {
                    targetShield[0] = 0;
                } else {
                    targetShield[0] = Math.abs(dmg);
                }
//                playSFX("reduce_shield");
            }
            updateShield(targetShield, targetShieldView);
            callback.onSuccess();

        } else {
            target.setHp(target.getHp() - dmg);
//            playSFX("take_damage");
        }

        updateHP(target, targetHP, targetHPBar, callback);

        Log.d(TAG, "Attack to " + target.getName() + "\nNew HP: " + target.getHp() + "/" + target.getMaxhp() + " | Shield: " + targetShield[0]);
    }

    /**
     * Recovers HP for the specified entity.
     * Uses a literal value for the effect 'cause it can be used too for the enemy.
     *
     * @param target
     * @param amount
     */
    private void heal(Entity target, int amount, TextView targetHP, HealthBarView targetHPBar, Callback_TCW callback) {
        final String TAG = "BattleScreen-heal";
        Log.d(TAG, "Healing target: " + target.getName());

        target.setHp(target.getHp() + amount);

//        playSFX("heal");

        updateHP(target, targetHP, targetHPBar, callback);

        Log.d(TAG, "Heal to " + target.getName() + "\nNew HP: " + target.getHp() + "/" + target.getMaxhp());
    }

    /**
     * Increase target shield by given amount.
     * Uses a literal value for the effect 'cause it can be used too for the enemy.
     *
     * @param shield
     * @param amount
     */
    private void increaseShield(int[] shield, int amount, TextView targetShield) {
        final String TAG = "BattleScreen-increaseShield";
        Log.d(TAG, "Increasing shield: +" + amount);

        shield[0] += amount;

//        playSFX("increase_shield");

        updateShield(shield, targetShield);
    }

    /**
     * Damages enemy by a certain amount. Also heals player by that amount.
     *
     * @param c
     * @param player
     * @param enemy
     * @param enemyShield
     */
    private void absorb(Card c, Player player, Enemy enemy, int[] enemyShield, TextView enemyShieldView, TextView playerHP, HealthBarView playerHPBar, TextView enemyHP, HealthBarView enemyHPBar, Callback_TCW callback) {
        final String TAG = "BattleScreen-absorb";
        Log.d(TAG, "Healing player: " + player.getName());
        heal(player, c.getEffect(), playerHP, playerHPBar, callback);

        Log.d(TAG, "Attacking to enemy: " + enemy.getName());
        attack(enemy, c.getEffect(), enemyShield, enemyShieldView, enemyHP, enemyHPBar, callback);
    }

    /**
     * Update UI for target's HP.
     * @param target
     * @param targetHP
     * @param targetHPBar
     */
    private void updateHP(Entity target, TextView targetHP, HealthBarView targetHPBar, Callback_TCW callback) {
        final String TAG = "BattleScreen-updateHP";
        Log.d(TAG, "Updating HP for: " + target.getName());

        // the text changes immediately
        targetHP.setText(target.getHp() + "/" + target.getMaxhp());

        // the bar changes progressively at a certain rate, depending on the difference
        // between the target hp and the hp set in the bar
        final long ANIMATION_DURATION = 1000;
        // used to set a speed rate for each HP point change
        int HPdifference = target.getHp() - targetHPBar.getHP();

        // only perform update if the HP actually changes
        if (HPdifference != 0) {
            long animationSpeedRate = ANIMATION_DURATION / HPdifference;
            Log.d(TAG, "Animation speed rate: "+animationSpeedRate);

            // if the target hp is lower, it'll decrease, if not, it'll increase
            int HPchangeRate = (target.getHp() < targetHPBar.getHP()) ? -1 : 1;
            Log.d(TAG, "HP change rate: "+HPchangeRate);

            // handler for delay changes
            Handler delayHandler = new Handler();

            // action to be run repeatedly
            Runnable updateHPRunnable = new Runnable() {
                @Override
                public void run() {
                    // until the hp in the bar and the target it's the same
                    if (targetHPBar.getHP() != target.getHp()) {
                        // update the HP bar
                        int newHP = targetHPBar.getHP() + HPchangeRate;

                        // ensure that new HP doesn't exceed the limits [0-targetMaxHP]
                        if ((HPchangeRate > 0 && newHP > target.getHp()) || (HPchangeRate < 0 && newHP < target.getHp())) {
                            newHP = target.getHp();
                        }

                        targetHPBar.setHP(newHP);

                        // schedule the next update
                        delayHandler.postDelayed(this, animationSpeedRate);
                    }

                    // if player hp decreases (has been attacked) set the card container to be clickable
                    if (target instanceof Player && HPchangeRate < 0) {
                        Log.d(TAG, "player hp finished decreasing");
                        // call to the callback on success method to notify activity that the
                        // turn has ended and another card can be played from then on
                        callback.onSuccess();
                    }
                }
            };
            delayHandler.post(updateHPRunnable);
        }
    }

    /**
     * Update UI for target's shield.
     *
     * @param shield
     * @param targetShield
     */
    private void updateShield(int[] shield, TextView targetShield) {
        final String TAG = "BattleScreen-updateShield";
        Log.d(TAG, "Updating shield");
//        new Handler().postDelayed(() -> {
//            targetShield.setText(shield[0]);
//        }, 300);

        targetShield.setText(String.valueOf(shield[0]));
    }

    /**
     * Change text of the narration view and play animation.
     *
     * @param text
     */
    private void showBattleNarration(String text) {
        narrationView.setText(text);
        Utils.playFadeInFadeOutAnimation(narrationView, 300, 700);
    }

    /**
     * Checks if there is a winner for the battle. Shows end battle dialog if there is.
     *
     * @param player
     * @param enemy
     * @return True if there is winner, false if not.
     */
    private boolean checkWinner(Player player, Enemy enemy) {
        final String TAG = "BattleScreen-checkWinner";
        boolean isThereWinner = false;

        if (!hasEndDialogBeenShown && (player.getHp() == 0 || enemy.getHp() == 0)) {
            Log.d(TAG, "-------------------------------- ENDED BATTLE --------------------------------");

            isThereWinner = true;
//            hasEndDialogBeenShown = true;

            if (enemy.getHp() == 0) {
                Log.d(TAG, "STAGE CLEARED");
                showEndStageDialog(getResources().getString(R.string.stage_cleared), 1000, enemy);

//                playSFX("plankton_aaa_meme");

                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(enemyImg, "alpha", 1f, 0f);
                fadeOut.setDuration(700);
                fadeOut.start();
            } else if (player.getHp() == 0) {
                Log.d(TAG, "GAME OVER");
                showEndGameDialog(getResources().getString(R.string.you_lost));

//                playSFX("loose");
            }
        // if there is no winner, but the player ran out of cards
        }

        if (!isThereWinner && playableCards.getChildCount() == 0) {
            Log.d(TAG, "NO MORE CARDS");
            showEndGameDialog(getResources().getString(R.string.no_more_cards));
        }

        return isThereWinner;
    }

    /**
     * Shows end game dialog with given text as title
     *
     * @param titleText
     */
    private void showEndGameDialog(String titleText) {
        final String TAG = "BattleScreen-showEndGameDialog";
        Log.d(TAG, "Showing end game dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View menu = getLayoutInflater().inflate(R.layout.battle_screen_end_menu, null);

        // set end battle menu title
        TextView title = menu.findViewById(R.id.menuTitle);
        title.setText(titleText);

        TextView towerProgress = menu.findViewById(R.id.towerProgress);
        towerProgress.setText("Tower: " + tower.getName() + "\nFloor: " + floor + "\nStage: " + (stage - (STAGES_TO_REST * (floor - 1))));

        // set tower coin image
        ImageView coinImg = menu.findViewById(R.id.coinImg);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("img/coins/tower.png"));
            coinImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error while getting bitmap image from assets: " + e);
        }

        TextView amount = menu.findViewById(R.id.amount);
        amount.setText("+"+(stage*EMOTION_COINS_PER_STAGE));

        // open pop up menu
        builder.setView(menu);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.battle_screen_menu_bg, null));

        new Handler().postDelayed(dialog::show, 700);

        dialog.setOnDismissListener(dialog1 -> {
            Log.d(TAG, "End game dialog dismissed.");
            endGame();
        });

        // exit and return to main menu
        // set exit battle functionality
        ExtendedFloatingActionButton returnToMainMenuButton = menu.findViewById(R.id.returnToMainMenuButton);
        returnToMainMenuButton.setOnClickListener(v1 -> {
            Log.d(TAG, "Return to main menu button pressed.");
            endGame();
        });
    }

    /**
     * Ends the game: unlocks the next tower and returns the player to the main menu.
     */
    private void endGame() {
        final String TAG = "BattleScreen-endGame";
        Log.d(TAG, "Ending game...");

        for (int i = 0; i < ALL_TOWERS.size(); i++) {
            // check for actual tower
            if (ALL_TOWERS.get(i).getId() == tower.getId()) {
                // try to get the next tower
                Tower nextTower = null;
                try {
                    nextTower = ALL_TOWERS.get(i+1);
                } catch (IndexOutOfBoundsException e) {
                    // if process fails then no more towers to unlock
                    Log.e(TAG, "No more towers to unlock.");
                }

                // only if there is a next tower
                if (nextTower != null) {
                    // unlock next tower
                    int[] playerUnlockedTowers = player.getUnlocked_towers();
                    playerUnlockedTowers = Arrays.copyOf(playerUnlockedTowers, playerUnlockedTowers.length + 1);
                    // store next tower's id in player unlocked towers
                    playerUnlockedTowers[playerUnlockedTowers.length - 1] = nextTower.getId();
                    // update player attribute
                    player.setUnlocked_towers(playerUnlockedTowers);
                    // restore player HP
                    player.restoreHP();
                    Log.d(TAG, "Unlocking new tower: "+nextTower.getName() + "\nSaving player data...");
                    // update local instance
                    PlayerManager.setInstance(player);
                    // save to local json file
                    PlayerManager.savePlayerData(this, player);
                    // TODO: SAVE TO REMOTE FROM LOCAL
                    if (mAuth.getCurrentUser() != null) {
                        Log.d(TAG, "Attempting to save local data to remote...");
                        PlayerManager.saveToRemoteFromLocal(this, mAuth.getCurrentUser());
                    }
                    break;
                }
            }
        }

        this.finish();

        Log.d(TAG, "Sending to home screen...");
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }

    /**
     * Shows end stage dialog with given text as title
     * @param titleText
     * @param delay
     * @param enemy
     */
    private void showEndStageDialog(String titleText, long delay, Enemy enemy) {
        final String TAG = "BattleScreen-showEndStageDialog";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View menu = getLayoutInflater().inflate(R.layout.battle_screen_end_stage_dialog, null);

        Log.d(TAG, "Setting up menu elements...");

        // set end battle menu title
        TextView title = menu.findViewById(R.id.menuTitle);
        title.setText(titleText);

        // set tower coin image
        ImageView coinImg = menu.findViewById(R.id.coinImg);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("img/coins/tower.png"));
            coinImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error while getting bitmap image from assets: " + e);
        }

        TextView amount = menu.findViewById(R.id.amount);
        amount.setText("+"+(stage*TOWER_COINS_PER_STAGE));

        Log.d(TAG, "Showing end stage dialog...");
        // open pop up menu
        builder.setView(menu);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.battle_screen_menu_bg, null));

        new Handler().postDelayed(dialog::show, delay);

        player.setTower_coins(player.getTower_coins()+TOWER_COINS_PER_STAGE);

        dialog.setOnDismissListener(dialog1 -> {
            Log.d(TAG, "End stage dialog dismissed.");
            // what to do when dismissing the STAGE END DIALOG (play next stage / rest (if applicable))
            if ((stage % STAGES_TO_REST) == 0) { // rest
                Log.d(TAG, stage + " % " + STAGES_TO_REST + " = " + (stage % STAGES_TO_REST));
                Log.d(TAG, "REST");

                rest(player, tower);

            } else if (stage >= TOTAL_STAGES) { // end game
                Log.d(TAG, "Tower ended, player won.");

                new Handler().postDelayed(() -> {
                    // add callback
                    Utils.playStoryNarration(this, storyNarrationBackground, storyNarrationView, tower.getDialogues(), new Callback_TCW() {
                        @Override
                        public void onSuccess() {
                            showEndGameDialog(getResources().getString(R.string.you_won));
                        }

                        @Override
                        public void onSuccess(Context context) {

                        }

                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });

                }, 1000);

            } else { // next stage
                Log.d(TAG, "Play next stage.");
                stage++;
                playStage(player, tower, floor, stage);
            }
        });
    }

    /**
     * Player rests: restore 1/4 of HP and is sent to in-game shop to buy cards with tower coins.
     * @param player
     * @param tower
     */
    private void rest(Player player, Tower tower) {
        final String TAG = "BattleScreen-rest";
        Log.d(TAG, "Player resting...");

        Log.d(TAG, "Restoring 1/4 of player's HP...");
        // player recovers 1/4 of its max hp
        // player HP + 1/4 of maxHP
        player.setHp(player.getHp()+(player.getMaxhp()/4));

        Log.d(TAG, "Sending player to in-game shop...");
        // TODO: SEND TO IN-GAME SHOP
        Intent intent = new Intent(this, InGameShop.class);
        intent.putExtra("currentTower", tower.getDialogues());
        // this launch wont have custom transition
        activityResultLauncher.launch(intent);

//        Utils.changeActivity(intent, this, R.anim.slide_out_left, R.anim.slide_in_right);
    }

    /**
     * Plays an enemy attack animation: Move Down (fast attack hit) -> Move Back (slightly slower return)
     */
    private void playEnemyAttackAnimation() {
        final String TAG = "BattleScreen-playEnemyAttackAnimation";
        Log.d(TAG, "Playing enemy attack animation...");
        // Move Down (fast attack hit)
        ObjectAnimator moveDown = ObjectAnimator.ofFloat(enemyImg, "translationY", 0f, 200f);
        moveDown.setDuration(80); // Fast attack impact
        moveDown.setInterpolator(new AccelerateInterpolator(1.5f));

        // Move Back (slightly slower return)
        ObjectAnimator moveUp = ObjectAnimator.ofFloat(enemyImg, "translationY", 120f, 0f);
        moveUp.setDuration(120); // Slightly slower return
        moveUp.setInterpolator(new DecelerateInterpolator(1.5f));

        // Combine animations
        AnimatorSet attackAnimation = new AnimatorSet();
        attackAnimation.playSequentially(moveDown, moveUp);
        attackAnimation.start();
    }
}