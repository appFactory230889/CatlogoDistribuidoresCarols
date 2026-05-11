package com.appfactory.catlogodistribuidorescarols;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SearchView;

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

public class conjuntosFalda extends AppCompatActivity {
    SearchView searchBar;
    FloatingActionButton fabAgregarPrenda;

    RecyclerView recyclerPrendas;
    DatabaseReference databaseReference;
    ArrayList<catalogoModel> catalogoModelList;
    catalogoAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conjuntos_falda);

        recyclerPrendas = findViewById(R.id.recyclerPrendas);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerPrendas.setLayoutManager(linearLayoutManager);
        catalogoModelList = new ArrayList<>();
        adapter = new catalogoAdapter(catalogoModelList);
        recyclerPrendas.setAdapter(adapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        searchBar = findViewById(R.id.searchBar);
        fabAgregarPrenda = findViewById(R.id.fabAgregarPrenda);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("CATALOGO").child("Conjuntos con Falda");
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
        });


        fabAgregarPrenda.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("Datos", MODE_PRIVATE);
            String categoria = "Conjuntos con Falda";

            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("categoria", categoria);
            editor.commit();
            startActivity(new Intent(conjuntosFalda.this, AgregarPrendas.class));
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