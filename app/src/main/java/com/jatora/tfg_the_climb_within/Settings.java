package com.jatora.tfg_the_climb_within;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.util.Locale;

public class Settings extends AppCompatActivity {
    private LanguagePreference languagePreference;

    private ActivityResultLauncher<Intent> googleSignInLauncher;

    // The actual button itself
    private SignInButton signInButton;
    private Button signOutButton;
    // Needed to connect to FirebaseAuth services
    private FirebaseAuth mAuth;

    // Used for OneTap Sign-In (Logging In)
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 361;  // Can be any integer unique to the Activity.

    // Used for Google Sign-In (Signing up)
    private GoogleSignInClient mGoogleSignInClient;
    private static final int REQ_GOOGLE_SIGN_IN = 362;  // Can be any integer unique to the Activity.

    // private boolean showOneTapUI = true;

//    private ImageButton setLangENButton;
//    private ImageButton setLangESButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String TAG = "Settings-onCreate()";

        super.onCreate(savedInstanceState);

//        languagePreference = new LanguagePreference(this);
//
//        applyLanguage();
//
//        Log.d(TAG, "Saved language: "+languagePreference.getLanguage());
//        Log.d(TAG, "Actual locale: "+ Locale.getDefault().getLanguage());

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        try {
                            firebaseAuthenticate(data);
                        } catch (ApiException e) {
                            Log.d("Login-signUp", "Authentication failed: " + e.getMessage());
                        }
                    } else {
                        Log.d("Login-signUp", "Sign-in failed or canceled.");
                    }
                }
        );


        signOutButton = findViewById(R.id.signOutButton);

        signOutButton.setOnClickListener(view -> signnOut());

        googleBtnUi();

        signInButton = findViewById(R.id.google_button);
        signInButton.setOnClickListener(view -> signIn());

        // Firebase Auth Instance
        mAuth = FirebaseAuth.getInstance();

        // Update UI depending if the user is logged in or not
        updateUI(this, mAuth.getCurrentUser(), 0);

        // Build the One Tap sign-in request
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Use your server's client ID, not the Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Optionally filter to only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        oneTapClient = Identity.getSignInClient(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's clFirebaseUser user = mAuth.getCurrentUser();
////                if(user != null) {
////                    PlayerManager.saveToRemoteFromLocal(getBaseContext(), user);
////                }ient ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.default_web_client_id))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, signInRequest);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                FirebaseUser user = mAuth.getCurrentUser();
//                if(user != null) {
//                    PlayerManager.saveToRemoteFromLocal(getBaseContext(), user);
//                }
//                finish();
//            }
//        });


//        setLangENButton = findViewById(R.id.setLangENButton);
//        setLangESButton = findViewById(R.id.setLangESButton);
//
//        boolean isLanguageEN = languagePreference.getLanguage().equalsIgnoreCase("en");
//
//        Log.d(TAG, "isLanguageEN: "+isLanguageEN);
//
//        setLangButtonsColors(isLanguageEN);
//
//        Player p = PlayerManager.getInstance(this);
//
//        // change language to english
//        setLangENButton.setOnClickListener(v -> {
//            // only change language to EN if it is not already set to EN (save processing)
//            if (!isLanguageEN) {
//                p.getSettings().setLanguage("en");
//                PlayerManager.setInstance(p);
//
//                languagePreference.setLanguage("en");
//                setLangButtonsColors(false);
//                recreate();
//            }
//        });
//
//        // change language to spanish
//        setLangESButton.setOnClickListener(v -> {
//            // only change language to ES if it is not already set to ES (save processing)
//            if (isLanguageEN) {
//                p.getSettings().setLanguage("es");
//                PlayerManager.setInstance(p);
//
//                languagePreference.setLanguage("es");
//                setLangButtonsColors(true);
//                recreate();
//            }
//        });

        ExtendedFloatingActionButton aboutButton = findViewById(R.id.aboutButton);

        aboutButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View menu = getLayoutInflater().inflate(R.layout.about_dialog, null);

            TextView version = menu.findViewById(R.id.version);
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String versionName = packageInfo.versionName;
                version.setText(getString(R.string.version)+versionName);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }


            // open pop up menu
            builder.setView(menu);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.battle_screen_menu_bg, null));
            dialog.show();

            // set return to battle functionality
            Button sourceButton = menu.findViewById(R.id.sourceButton);
            sourceButton.setOnClickListener(v1 -> {
                // send user to github repo
                final String githubRepoURL = "https://github.com/crrauldam/TFG-The_Climb_Within";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(githubRepoURL));
                startActivity(intent);
            });

            // set return to battle functionality
            Button institutionButton = menu.findViewById(R.id.institutionButton);
            institutionButton.setOnClickListener(v1 -> {
                // send user to github repo
                final String institutionURL = "https://site.educa.madrid.org/ies.juandelacierva.madrid/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(institutionURL));
                startActivity(intent);
            });

            TextView ja = menu.findViewById(R.id.ja);
            ja.setOnLongClickListener(vja -> {
                Toast.makeText(this, getResources().getString(R.string.ja_quote1), Toast.LENGTH_SHORT).show();
                return true;
            });

            TextView to = menu.findViewById(R.id.to);
            to.setOnLongClickListener(vja -> {
                Toast.makeText(this, getResources().getString(R.string.to_quote1), Toast.LENGTH_SHORT).show();
                return true;
            });

            TextView ra = menu.findViewById(R.id.ra);
            ra.setOnLongClickListener(vja -> {
                Toast.makeText(this, getResources().getString(R.string.ra_quote1), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, getResources().getString(R.string.ra_quote2), Toast.LENGTH_SHORT).show();
                return true;
            });

        });
    }
//
//    /**
//     * Set button colors depending on the language.
//     * @param isLanguageEN
//     */
//    private void setLangButtonsColors(boolean isLanguageEN) {
//        if (isLanguageEN) {
//            // set EN button as SELECTED
//            setLangENButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_cyan)));
//            // set ES button as UNSELECTED
//            setLangESButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.payne_gray)));
//        } else {
//            // set ES button as SELECTED
//            setLangESButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_cyan)));
//            // set EN button as UNSELECTED
//            setLangENButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.payne_gray)));
//        }
//    }
//
//    /**
//     * Apply language preference
//     */
//    private void applyLanguage() {
//        String languageCode = languagePreference.getLanguage();
//        LocaleHelper.updateLocale(this, languageCode);
//    }
//
//    // Ensure locale is applied before attaching the base context
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        LanguagePreference pref = new LanguagePreference(newBase);
//        super.attachBaseContext(LocaleHelper.updateLocale(newBase, pref.getLanguage()));
//    }

    private void signUp() {
        try {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        final String TAG = "Settings-signUp()";
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Log.d(TAG, String.valueOf(signInIntent));
        Log.d(TAG, String.valueOf(googleSignInLauncher));
        googleSignInLauncher.launch(signInIntent);
    }

    private void signIn() {
        Log.d("Login-signIn", "signIn() called");
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("Login-signIn", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Handle failure (e.g., no saved credentials)
                    Log.e("Login-signIn", "One Tap sign-in failed: " + e.getLocalizedMessage());
//                    Toast.makeText(this, "No account found! Signing up...", Toast.LENGTH_SHORT).show();
                    signUp();
                });
    }

    // Not currently needed
//    private void signUpWithGoogle() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQ_GOOGLE_SIGN_IN);
//    }

    private void googleBtnUi() {
        SignInButton googleButton = (SignInButton) findViewById(R.id.google_button);
        googleButton.setOnClickListener(view -> signIn());

        for (int i = 0; i < googleButton.getChildCount(); i++) {
            View v = googleButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(14);
                //tv.setTypeface(null, Typeface.NORMAL);
                tv.setText(R.string.sign_up_with_google);
                //tv.setTextColor(Color.parseColor("#FFFFFF"));
                //tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_launcher));
                //tv.setSingleLine(true);
                //tv.setPadding(15, 15, 15, 15);

//                ViewGroup.LayoutParams params = tv.getLayoutParams();
//                params.width = 100;
//                params.height = 70;
//                tv.setLayoutParams(params);

                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("Login-onActivityResult", "onActivityResult() called");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate with Firebase.
                    Log.d("Login-onActivityResult", "Got ID token: " + idToken);
                    firebaseAuthenticate(data);
                }
            } catch (ApiException e) {
                Log.d("Login-onActivityResult", "Failed to get ID Token. > " + e.getMessage());
            }
        }
    }

    private void firebaseAuthenticate(@Nullable Intent data) throws ApiException {
        SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(data);
        String idToken = googleCredential.getGoogleIdToken();
        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate
            // with Firebase.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Login-firebaseAuthenticate", "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(Settings.this, user, 0);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Login-firebaseAuthenticate", "signInWithCredential:failure > ", task.getException());
                                updateUI(Settings.this, null, 0);
                            }
                        }
                    });
        }
    }

    private void signnOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            updateUI(this, mAuth.getCurrentUser(), 1);
        });

    }

    private void updateUI(Context context, FirebaseUser user, int state) {
        final String TAG = "Settings-updateUI()";
        if(state == 0) {
            if (user != null) {
                Log.d(TAG, "updateUI() called on signIn");
                SignInButton googleButton = (SignInButton) findViewById(R.id.google_button);
                googleButton.setVisibility(View.INVISIBLE);
//                Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
                TextView userId = findViewById(R.id.userId);
                userId.setText(user.getEmail());
                userId.setVisibility(View.VISIBLE);
                signOutButton.setVisibility(View.VISIBLE);
                PlayerManager.checkRemotePlayerData(context, user, true);
            } else {
//                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                signOutButton.setVisibility(View.INVISIBLE);
            }
        }else if (state == 1) {
            if (user == null) {
                Log.d(TAG, "updateUI() called on signOut");
                SignInButton googleButton = (SignInButton) findViewById(R.id.google_button);
                googleButton.setVisibility(View.VISIBLE);
//                Toast.makeText(this, "SignOut successful.", Toast.LENGTH_SHORT).show();
                TextView userId = findViewById(R.id.userId);
                userId.setVisibility(View.INVISIBLE);
                signOutButton.setVisibility(View.INVISIBLE);
            } else {
//                Toast.makeText(this, "SignOut failed", Toast.LENGTH_SHORT).show();
            }

        }
    }
}