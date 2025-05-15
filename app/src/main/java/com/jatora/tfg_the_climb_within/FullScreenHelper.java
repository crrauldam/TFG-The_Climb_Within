package com.jatora.tfg_the_climb_within;

import android.app.Activity;
import android.view.View;
import android.view.Window;

public class FullScreenHelper {
    private final Activity activity;

    public FullScreenHelper(Activity activity) {
        this.activity = activity;
    }

    // Método para activar el modo inmersivo adhesivo (STICKY)
    public void enableFullScreen() {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();

        // Extender contenido detrás de las barras del sistema
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        // Ocultar barras en modo inmersivo adhesivo
        hideSystemUI();
    }

    private void hideSystemUI() {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            hideSystemUI();
        }
    }
}
