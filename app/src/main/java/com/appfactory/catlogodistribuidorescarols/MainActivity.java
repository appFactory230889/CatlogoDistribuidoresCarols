
package com.appfactory.catlogodistribuidorescarols;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ImageView imgTemporada, icoDeConjuntosConFalda, iconoConjuntosConPantalon, iconoEnterizos, iconoFaldas, iconoTops, iconoVestidosCortos, iconoVestidosLargos, iconoOtrosEstilos;
    Button btnAgregarPrenda, btnBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        icoDeConjuntosConFalda = findViewById(R.id.icoDeConjuntosConFalda);
        iconoConjuntosConPantalon = findViewById(R.id.iconoConjuntosConPantalon);
        iconoEnterizos = findViewById(R.id.iconoEnterizos);
        iconoFaldas = findViewById(R.id.iconoFaldas);
        iconoTops = findViewById(R.id.iconoTops);
        iconoVestidosCortos = findViewById(R.id.iconoVestidosCortos);
        iconoVestidosLargos = findViewById(R.id.iconoVestidosLargos);
        iconoOtrosEstilos = findViewById(R.id.iconoOtrosEstilos);
        btnAgregarPrenda = findViewById(R.id.btnAgregarPrenda);
        btnBusqueda = findViewById(R.id.btnBusqueda);
        imgTemporada = findViewById(R.id.imgTemporada);

        btnAgregarPrenda.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AgregarPrendas.class));
        });

        btnBusqueda.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, busquedaGeneral.class));

        });


        icoDeConjuntosConFalda.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, conjuntosFalda.class));
        });

        iconoConjuntosConPantalon.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, conjuntosPantalon.class));
        });

        iconoEnterizos.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, enterizos.class));
        });

        iconoFaldas.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,faldas.class));
        });

        iconoTops.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,tops.class));
        });

        iconoVestidosCortos.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, vestidosCortos.class));
        });

        iconoVestidosLargos.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, vestidosLargos.class));
        });

        iconoOtrosEstilos.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, otrosEstilos.class));
        });

        imgTemporada.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Temporada.class));
        });


    }
    /// ////////////////////////////////////////////////////////////////////////////////////////


}