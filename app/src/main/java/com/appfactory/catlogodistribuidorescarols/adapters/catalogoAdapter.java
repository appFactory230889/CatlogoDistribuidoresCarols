package com.appfactory.catlogodistribuidorescarols.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appfactory.catlogodistribuidorescarols.EditorDeItems;
import com.appfactory.catlogodistribuidorescarols.R;
import com.appfactory.catlogodistribuidorescarols.models.catalogoModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class catalogoAdapter extends RecyclerView.Adapter<catalogoAdapter.ViewHolderCatalogoAdapter>{
ArrayList<catalogoModel> catalogoModelList;
public catalogoAdapter(ArrayList<catalogoModel> catalogoModelList){
    this.catalogoModelList = catalogoModelList;
}
@NonNull
@Override
public ViewHolderCatalogoAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prendas_view, parent, false);
    ViewHolderCatalogoAdapter viewHolderCatalogoAdapter = new ViewHolderCatalogoAdapter(view);
    return viewHolderCatalogoAdapter;
}

@Override
public void onBindViewHolder(@NonNull ViewHolderCatalogoAdapter holder, int position) {
    catalogoModel catalogoModel = catalogoModelList.get(position);
    holder.tvPrecio1.setText("Precio de xxs a l: $"+catalogoModelList.get(position).getPrecio1());
    holder.tvPrecio2.setText("Precio de xl a xxl: $"+catalogoModelList.get(position).getPrecio2());
    holder.itemView.setClickable(false);//Para deshabilitar la capacidad de clic en cualquier parte del espacio del view
    holder.precio1 = catalogoModelList.get(position).getPrecio1();
    holder.precio2 = catalogoModelList.get(position).getPrecio2();
    holder.codigo = catalogoModelList.get(position).getCodigo();
    holder.foto = catalogoModelList.get(position).getFoto();
    holder.categoria = catalogoModelList.get(position).getCategoria();
    holder.setOnClickListener();
    String imagenPrenda = catalogoModel.getFoto();

    holder.imgDownLoad.setOnClickListener(v -> {
        descargarImagen(v.getContext(), catalogoModel.getCodigo(), catalogoModel.getFoto());
    });

    try {
        if (!imagenPrenda.equals(""))
            Picasso.get()
                    .load(imagenPrenda)
                    .into(holder.imgFotoPrenda);
    } catch (Exception e) {
        Log.d("Exeption", "e: " + e);
    }
}
@Override
public int getItemCount() {
    return catalogoModelList.size();
}

public class ViewHolderCatalogoAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView imgFotoPrenda, imgDownLoad;
    TextView tvPrecio1, tvPrecio2;
    Button btnEditarPrenda;
    String precio1, precio2, codigo, foto, categoria;
    Context context;

    public ViewHolderCatalogoAdapter(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        imgFotoPrenda = itemView.findViewById(R.id.imgFotoPrenda);
        imgDownLoad = itemView.findViewById(R.id.imgDownLoad);

        tvPrecio1 = itemView.findViewById(R.id.tvPrecio1);
        tvPrecio2 = itemView.findViewById(R.id.tvPrecio2);
        btnEditarPrenda = itemView.findViewById(R.id.btnEditarPrenda);
        context = itemView.getContext();
    }
    public void setOnClickListener(){
        btnEditarPrenda.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        SharedPreferences preferences = context.getSharedPreferences("Datos", MODE_PRIVATE);
        String precio1 = this.precio1;
        String precio2 = this.precio2;
        String codigo = this.codigo;
        String foto = this.foto;
        String categoria = this.categoria;

        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("precio1", precio1);
        editor.putString("precio2", precio2);
        editor.putString("codigo", codigo);
        editor.putString("foto", foto);
        editor.putString("categoria", categoria);
        editor.commit();
        Intent intent = new Intent(context, EditorDeItems.class);
        context.startActivity(intent);
        }
    }

    private void descargarImagen(Context context, String nombre, String url) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url);

        // 1️⃣ Crear nombre del archivo
        String nombreArchivo = nombre + "_" + System.currentTimeMillis() + ".jpg";

        // 2️⃣ Usar MediaStore para guardar directamente en la galería
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CatalogoCarol");

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri == null) {
            Toast.makeText(context, "No se pudo acceder al almacenamiento", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3️⃣ Abrir flujo de salida
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);

            // 4️⃣ Descargar la imagen de Firebase como bytes
            storageRef.getBytes(10 * 1024 * 1024) // límite: 10MB
                    .addOnSuccessListener(bytes -> {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.close();

                            Toast.makeText(context, "✅ Imagen guardada en la galería", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "❌ Error al descargar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al abrir flujo de salida", Toast.LENGTH_SHORT).show();
        }
    }
}
