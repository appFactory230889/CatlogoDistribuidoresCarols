package com.appfactory.catlogodistribuidorescarols;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appfactory.catlogodistribuidorescarols.adapters.catalogoAdapter;
import com.appfactory.catlogodistribuidorescarols.models.catalogoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Temporada extends AppCompatActivity {
    SearchView searchBar;
    FloatingActionButton fabAgregarPrenda;
    Spinner spinnerTemporadas;

    List<String> periodos;
    ArrayAdapter<String> adapterPeriodos;

    RecyclerView recyclerPrendas;
    private ValueEventListener registrosListener;
    DatabaseReference databaseReference;
    ArrayList<catalogoModel> catalogoModelList;
    catalogoAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference mDatabase;

    String periodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_temporada);

        recyclerPrendas = findViewById(R.id.recyclerPrendas);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerPrendas.setLayoutManager(linearLayoutManager);
        catalogoModelList = new ArrayList<>();
        adapter = new catalogoAdapter(catalogoModelList);
        recyclerPrendas.setAdapter(adapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        searchBar = findViewById(R.id.searchBar);
        fabAgregarPrenda = findViewById(R.id.fabAgregarPrenda);
        spinnerTemporadas = findViewById(R.id.spinnerTemporadas);

        periodos = new ArrayList<>();
        adapterPeriodos = new ArrayAdapter<>(Temporada.this, android.R.layout.simple_spinner_dropdown_item, periodos);
        spinnerTemporadas.setAdapter(adapterPeriodos);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("CATALOGO").child("Listado Temporadas");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                periodos.clear(); // Limpiar antes de volver a llenar
                periodos.add("");
                for (DataSnapshot data : snapshot.getChildren()) {
                    String periodo = data.child("referencia").getValue(String.class);
                    if (periodo != null && !periodo.trim().isEmpty()) {
                        periodos.add(periodo);
                    }
                }
                adapterPeriodos.notifyDataSetChanged(); // Refrescar el spinner
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Temporada.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        spinnerTemporadas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                periodo = parent.getItemAtPosition(position).toString();
                if (periodo.trim().isEmpty()) {
                    catalogoModelList.clear();
                    adapter.notifyDataSetChanged();
                    return;
                }
                cargarRegistros();
                /*establecerFechaHora();*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /*databaseReference = FirebaseDatabase.getInstance().getReference().child("CATALOGO").child("Julio");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    catalogoModelList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        catalogoModel catalogoModel = snapshot1.getValue(catalogoModel.class);
                        catalogoModelList.add(catalogoModel);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });*/


        fabAgregarPrenda.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("Datos", MODE_PRIVATE);
            String categoria = "Conjuntos con Falda";

            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("categoria", categoria);
            editor.commit();
            startActivity(new Intent(Temporada.this, AgregarPrendas.class));
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                buscar (s);
                return true;
            }
        });

    }
    /// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void cargarRegistros() {

        if (periodo == null || periodo.isEmpty()) {
            return;
        }

        // Elimina el listener anterior
        if (databaseReference != null && registrosListener != null) {
            databaseReference.removeEventListener(registrosListener);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("CATALOGO").child("Temporadas").child(periodo);

        registrosListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                catalogoModelList.clear();

                if (snapshot.exists()) {

                    for (DataSnapshot data : snapshot.getChildren()) {

                        catalogoModel modelo = data.getValue(catalogoModel.class);

                        if (modelo != null) {
                            catalogoModelList.add(modelo);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Temporada.this,
                        error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.addValueEventListener(registrosListener);
    }

    private void buscar(String s) {
        ArrayList<catalogoModel>milista = new ArrayList<>();
        for (catalogoModel obj: catalogoModelList){
            if(obj.getSeo().toLowerCase().contains(s.toLowerCase())){
                milista.add(obj);
            }
        }
        catalogoAdapter adapter = new catalogoAdapter(milista);
        recyclerPrendas.setAdapter(adapter);
    }
}
