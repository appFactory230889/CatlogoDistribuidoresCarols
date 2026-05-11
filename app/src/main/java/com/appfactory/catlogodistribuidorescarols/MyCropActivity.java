package com.appfactory.catlogodistribuidorescarols;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.yalantis.ucrop.UCropActivity;

public class MyCropActivity extends UCropActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ocultarSystemUI();

        Toolbar toolbar = findViewById(com.yalantis.ucrop.R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(null); // elimina botón X
        }
    }

    private void ocultarSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ocultarSystemUI();
        }
    }
}