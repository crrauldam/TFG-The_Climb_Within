package com.jatora.tfg_the_climb_within;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Settings extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        signOutButton = findViewById(R.id.signOutButton);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signnOut();
            }
        });

        // Register the launcher here, before it's used
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Handle the result here
                    if (result.getResultCode() == Settings.RESULT_OK) {
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        googleSignInLauncher.launch(signInIntent);
                    }
                }
        );

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

    }
    private void signUp() {
        try {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                    Toast.makeText(this, "No account found! Signing up...", Toast.LENGTH_SHORT).show();
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
                tv.setText("Sign Up with Google");
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
                Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
                TextView userId = findViewById(R.id.userId);
                userId.setText(user.getEmail());
                userId.setVisibility(View.VISIBLE);
                signOutButton.setVisibility(View.VISIBLE);
                PlayerManager.checkRemotePlayerData(context, user);
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                signOutButton.setVisibility(View.INVISIBLE);
            }
        }else if (state == 1) {
            if (user == null) {
                Log.d(TAG, "updateUI() called on signOut");
                SignInButton googleButton = (SignInButton) findViewById(R.id.google_button);
                googleButton.setVisibility(View.VISIBLE);
                Toast.makeText(this, "SignOut successful.", Toast.LENGTH_SHORT).show();
                TextView userId = findViewById(R.id.userId);
                userId.setVisibility(View.INVISIBLE);
                signOutButton.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(this, "SignOut failed", Toast.LENGTH_SHORT).show();
            }

        }

    }


}