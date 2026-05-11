package com.appfactory.catlogodistribuidorescarols;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {

    String result;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);

        ocultarSystemUI(); // 🔹 ocultamos desde el inicio
        readIntent();

        if (fileUri == null) {
            Toast.makeText(this, "No se encontró la imagen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String dest_uri = UUID.randomUUID().toString() + ".jpg";
        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);

        // 🚀 lanzamos UCrop normal (no a esta misma Activity)
        /*UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                .withOptions(options)
                .withAspectRatio(0, 0)
                .useSourceImageAspectRatio()
                .withMaxResultSize(5000, 5000)
                .start(CropperActivity.this);*/

        Intent intent = UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                .withOptions(options)
                .withAspectRatio(0, 0)
                .useSourceImageAspectRatio()
                .withMaxResultSize(5000, 5000)
                .getIntent(this);

// 👇 aquí reemplazamos UCropActivity por nuestra MyCropActivity
        intent.setClass(this, MyCropActivity.class);

        startActivityForResult(intent, UCrop.REQUEST_CROP);
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
            ocultarSystemUI(); // 🔹 se vuelve a aplicar si el usuario toca la pantalla
        }
    }

    private void readIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("DATA")) {
            result = intent.getStringExtra("DATA");
            if (result != null) {
                fileUri = Uri.parse(result);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", String.valueOf(resultUri));
            setResult(RESULT_OK, returnIntent);
            finish();
        } else if (resultCode == RESULT_CANCELED) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
}
