package com.appfactory.catlogodistribuidorescarols;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditorDeItems extends AppCompatActivity {
    Button btnEliminar, btnGuardar;
    ImageView imgFotoPrenda;
    EditText edtPrecio1, edtPrecio2;
    String foto, categoria, codigo;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_de_items);

        btnEliminar = findViewById(R.id.btnEliminar);
        btnGuardar = findViewById(R.id.btnGuardar);
        imgFotoPrenda = findViewById(R.id.imgFotoPrenda);
        edtPrecio1 = findViewById(R.id.edtPrecio1);
        edtPrecio2 = findViewById(R.id.edtPrecio2);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        cargarDatosDeSharedPreferences();

        try {
            if (!foto.equals(""))
                Picasso.get()
                        .load(foto)
                        .into(imgFotoPrenda);
        } catch (Exception e) {
            Log.d("Exeption", "e: " + e);
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String precio1 = edtPrecio1.getText().toString();
                String precio2 = edtPrecio2.getText().toString();

                Map<String, Object> datosItem = new HashMap<>();
                datosItem.put("precio1", precio1);
                datosItem.put("precio2", precio2);

                mDatabase.child("CATALOGO").child(categoria).child(codigo).updateChildren(datosItem);
                mDatabase.child("CATALOGO").child("Todas las prendas").child(codigo).updateChildren(datosItem);
                finish();
                Toast.makeText(EditorDeItems.this, "Item Actualizado!", Toast.LENGTH_SHORT).show();

            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoConfirmacion();

            }
        });

    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void cargarDatosDeSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("Datos", MODE_PRIVATE);
        String precio1 = preferences.getString("precio1", "Cargando Datos...");
        String precio2 = preferences.getString("precio2", "Cargando Datos...");
        String cod = preferences.getString("codigo", "Cargando Datos...");
        foto = preferences.getString("foto", "Cargando Datos...");
        categoria = preferences.getString("categoria", "Cargando Datos...");
        edtPrecio1.setText(precio1);
        edtPrecio2.setText(precio2);
        codigo = cod;
    }
    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar eliminación")
                .setMessage("Confirma que desea eleminar este item?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 👉 Aquí va tu código de eliminación
                        mDatabase.child("CATALOGO").child(categoria).child(codigo).removeValue();
                        mDatabase.child("CATALOGO").child("Todas las prendas").child(codigo).removeValue();
                        finish();
                        Toast.makeText(EditorDeItems.this, "Item Eliminado!", Toast.LENGTH_SHORT).show();
                        /*Intent intent = new Intent(EditorDeItems.this, MainActivity.class);
                        startActivityForResult(intent, 0);*/

                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        builder.create().show();
    }

}