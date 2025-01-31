package com.jatora.tfg_the_climb_within;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.CredentialManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

public class Settings extends AppCompatActivity {

//    private static final int RC_SIGN_IN = 123;
//    private GoogleSignInClient googleSignInClient;
//    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private TextView userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up the Sign-In button
        SignInButton signInButton = findViewById(R.id.bt_sign_in);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(view -> signIn());

        // TextView to display user info

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign-In account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        }
    }
}


//        // Configurar Google Sign-In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.client_id))
//                .requestEmail()
//                .build();
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso);
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        findViewById(R.id.bt_sign_in).setOnClickListener(view -> signInGoogle());
//    }
//
//    private void signInGoogle() {
//        Intent signInIntent = googleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            try {
//                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
//                if (account != null) firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                Log.w("SignIn", "Google sign-in failed", e);
//            }
//        }
//    }
//
//    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = firebaseAuth.getCurrentUser();
//                        Toast.makeText(this, "¡Inicio de sesión exitoso! Bienvenido " + user.getDisplayName(), Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(this, "Falló el inicio de sesión", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//
