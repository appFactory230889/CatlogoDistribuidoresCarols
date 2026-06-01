package com.appfactory.catlogodistribuidorescarols;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AgregarPrendas extends AppCompatActivity {

    EditText edtPrecio1, edtPrecio2;
    Spinner spinnerClasificacion;
    ImageView imgFotoPrenda;
    TextView tvCodigo, txtNumeroBorde, tvAlertaPrecio1, tvAlertaPrecio2, tvClasificacion, tvAlertaClasificacion;
    Button btnAgregarPrenda, btnFotoPrenda, btnFinalizar;
    FrameLayout frameCaptura;
    ProgressBar progressBar;

    Bitmap thumb_bitmap = null;
    Bitmap imagenCapturada = null;

    StorageReference storageReference;
    DatabaseReference mDatabase;

    String categoria, codigo;

    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_prendas);

        // 🔹 UI
        edtPrecio1 = findViewById(R.id.edtPrecio1);
        edtPrecio2 = findViewById(R.id.edtPrecio2);
        imgFotoPrenda = findViewById(R.id.imgFotoPrenda);
        tvCodigo = findViewById(R.id.tvCodigo);
        txtNumeroBorde = findViewById(R.id.txtNumeroBorde);
        spinnerClasificacion = findViewById(R.id.spinnerClasificacion);
        btnAgregarPrenda = findViewById(R.id.btnAgregarPrenda);
        btnFotoPrenda = findViewById(R.id.btnFotoProducto);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        frameCaptura = findViewById(R.id.frameCaptura);
        progressBar = findViewById(R.id.progressBar);
        tvAlertaPrecio1 = findViewById(R.id.tvAlertaPrecio1);
        tvAlertaPrecio2 = findViewById(R.id.tvAlertaPrecio2);
        tvClasificacion = findViewById(R.id.tvClasificacion);
        tvAlertaClasificacion = findViewById(R.id.tvAlertaClasificacion);
        tvAlertaClasificacion.setVisibility(View.GONE);
        tvAlertaPrecio1.setVisibility(View.GONE);
        tvAlertaPrecio2.setVisibility(View.GONE);


        btnAgregarPrenda.setVisibility(View.GONE);
        btnFinalizar.setVisibility(View.GONE);

        // 🔹 Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("Fotos Subidas");

        setSpinnerClasificacion();

        // 🔹 Selector imagen
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        Intent intent = new Intent(this, CropperActivity.class);
                        intent.putExtra("DATA", result.toString());
                        startActivityForResult(intent, 101);
                    }
                });

        btnFotoPrenda.setOnClickListener(v -> mGetContent.launch("image/*"));

        btnAgregarPrenda.setOnClickListener(v -> validarYGuardar());
    }

    ////////////////////////////////////////////////////////////Hasta Aqui el OnCreate//////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void vibrarTelefono() {
        Context context = AgregarPrendas.this; // 👈 este es el context correcto en un Fragment

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            Vibrator vibrator = vibratorManager.getDefaultVibrator();
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500); // para dispositivos antiguos
            }
        }
    }

    private void parpadearVista(View view) {
        // Animación de parpadeo
        Animation blinkAnimation = new AlphaAnimation(0.0f, 1.0f);
        blinkAnimation.setDuration(150);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(5);
        view.startAnimation(blinkAnimation);

        if (view instanceof ImageView) {
            // Caso de imagen
            ((ImageView) view).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            new Handler().postDelayed(() -> ((ImageView) view).clearColorFilter(), 900);
        } else if (view.getBackground() != null) {
            // Solo aplicar si tiene fondo
            view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            new Handler().postDelayed(() -> view.getBackground().clearColorFilter(), 900);
        } else {
            // 🔹 Si no tiene background (ejemplo Spinner en algunos temas), cambiamos el texto
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                int originalColor = tv.getCurrentTextColor();
                tv.setTextColor(Color.RED);
                new Handler().postDelayed(() -> tv.setTextColor(originalColor), 900);
            }
        }
    }

    private void validarYGuardar() {

        if (categoria == null || categoria.isEmpty()) {
            vibrarTelefono();
            parpadearVista(tvClasificacion);
            tvAlertaClasificacion.setVisibility(View.VISIBLE);
            parpadearVista(tvAlertaClasificacion);
            return;
        } else {
            tvAlertaClasificacion.setVisibility(View.GONE);
        }

        String precio1 = edtPrecio1.getText().toString().trim();
        if (precio1.isEmpty()) {
            edtPrecio1.setError("Ingrese el precio 1");
            Toast.makeText(AgregarPrendas.this, "Ingrese el precio 1", Toast.LENGTH_SHORT).show();
            vibrarTelefono();
            parpadearVista(edtPrecio1);
            tvAlertaPrecio1.setVisibility(View.VISIBLE);
            parpadearVista(tvAlertaPrecio1);
            return;
        } else {
            tvAlertaPrecio1.setVisibility(View.GONE);
        }

        String precio2 = edtPrecio2.getText().toString().trim();
        if (precio2.isEmpty()) {
            edtPrecio2.setError("Ingrese el precio 2");
            Toast.makeText(AgregarPrendas.this, "Ingrese el precio 2", Toast.LENGTH_SHORT).show();
            vibrarTelefono();
            parpadearVista(edtPrecio2);
            tvAlertaPrecio2.setVisibility(View.VISIBLE);
            parpadearVista(tvAlertaPrecio2);
            return;
        } else {
            tvAlertaPrecio2.setVisibility(View.GONE);
        }



        traerCodigoDisponible(); // 🔥 SOLO ESTO
    }

    ////////////////////////////////////////////////////////////

    private void traerCodigoDisponible() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("CATALOGO")
                .child("Codigo");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) return;

                codigo = snapshot.child("codDisponible")
                        .getValue(String.class);

                if (codigo == null) return;

                tvCodigo.setText(codigo);
                txtNumeroBorde.setText(codigo);

                capturarArea(); // 🔥 AQUÍ se captura
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    ////////////////////////////////////////////////////////////

    private void capturarArea() {

        frameCaptura.post(() -> {

            Bitmap bitmap = Bitmap.createBitmap(
                    frameCaptura.getWidth(),
                    frameCaptura.getHeight(),
                    Bitmap.Config.ARGB_8888
            );

            Canvas canvas = new Canvas(bitmap);
            frameCaptura.draw(canvas);

            imagenCapturada = bitmap;

            progressBar.setVisibility(View.VISIBLE);

            subirCapturaAFirebase();
        });
    }

    ////////////////////////////////////////////////////////////

    private void subirCapturaAFirebase() {

        if (imagenCapturada == null) return;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagenCapturada.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        String nombre = "captura_" + System.currentTimeMillis() + ".jpg";
        StorageReference ref = storageReference.child(nombre);

        ref.putBytes(data)
                .continueWithTask(task -> ref.getDownloadUrl())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) return;

                    Uri downloadUri = task.getResult();
                    guardarDatosEnDatabase(downloadUri.toString());
                });
    }

    ////////////////////////////////////////////////////////////

    private void guardarDatosEnDatabase(String urlImagen) {

        String precio1 = edtPrecio1.getText().toString();
        String precio2 = edtPrecio2.getText().toString();

        int nuevoCodigo = Integer.parseInt(codigo) + 1;

        Map<String, Object> codigoMap = new HashMap<>();
        codigoMap.put("codDisponible", String.valueOf(nuevoCodigo));

        mDatabase.child("CATALOGO")
                .child("Codigo")
                .updateChildren(codigoMap);

        Map<String, Object> datos = new HashMap<>();
        datos.put("foto", urlImagen);
        datos.put("precio1", precio1);
        datos.put("precio2", precio2);
        datos.put("codigo", codigo);
        datos.put("categoria", categoria);
        datos.put("seo", codigo + "_" + categoria);

        mDatabase.child("CATALOGO").child("Todas las prendas").child(codigo).setValue(datos);

        mDatabase.child("CATALOGO").child(categoria).child(codigo).setValue(datos);

        mDatabase.child("CATALOGO").child("Junio").child(codigo).setValue(datos)

                .addOnSuccessListener(aVoid -> {

                    progressBar.setVisibility(View.GONE);

                    imgFotoPrenda.setImageResource(R.drawable.logo);
                    edtPrecio1.setText("");
                    edtPrecio2.setText("");
                    tvCodigo.setText("");
                    txtNumeroBorde.setText("");
                    spinnerClasificacion.setSelection(0);

                    btnFinalizar.setVisibility(View.VISIBLE);
                    btnAgregarPrenda.setVisibility(View.GONE);

                    Toast.makeText(this,
                            "Añadido al catálogo con éxito",
                            Toast.LENGTH_SHORT).show();
                });
    }

    ////////////////////////////////////////////////////////////

    private void setSpinnerClasificacion() {

        List<String> lista = Arrays.asList("", "Conjuntos con Falda", "Conjuntos con Pantalon", "Enterizos", "Faldas", "Tops", "Vestidos Cortos", "Vestidos Largos", "Otros Estilos");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                lista);

        spinnerClasificacion.setAdapter(adapter);

        spinnerClasificacion.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        categoria = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
    }

    ////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 101) {

            String result = data.getStringExtra("RESULT");

            if (result != null) {

                Uri resultUri = Uri.parse(result);

                imgFotoPrenda.setImageURI(resultUri);

                try {
                    thumb_bitmap = MediaStore.Images.Media
                            .getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                btnAgregarPrenda.setVisibility(View.VISIBLE);
            }
        }
    }
}