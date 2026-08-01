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
import android.os.Handler;
import android.os.Looper;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

public class catalogoAdapter extends RecyclerView.Adapter<catalogoAdapter.ViewHolderCatalogoAdapter> {
    ArrayList<catalogoModel> catalogoModelList;

    public catalogoAdapter(ArrayList<catalogoModel> catalogoModelList) {
        this.catalogoModelList = catalogoModelList;
    }

    @NonNull
    @Override
    public ViewHolderCatalogoAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prendas_view, parent, false);
        return new ViewHolderCatalogoAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCatalogoAdapter holder, int position) {
        catalogoModel catalogoModel = catalogoModelList.get(position);
        holder.tvPrecio1.setText("Precio de xxs a l: $" + catalogoModel.getPrecio1());
        holder.tvPrecio2.setText("Precio de xl a xxl: $" + catalogoModel.getPrecio2());
        holder.itemView.setClickable(false);
        holder.precio1 = catalogoModel.getPrecio1();
        holder.precio2 = catalogoModel.getPrecio2();
        holder.codigo = catalogoModel.getCodigo();
        holder.foto = catalogoModel.getFoto();
        holder.categoria = catalogoModel.getCategoria();
        holder.setOnClickListener();

        String imagenPrenda = catalogoModel.getFoto();
        holder.imgDownLoad.setOnClickListener(v ->
                descargarImagen(v.getContext(), catalogoModel.getCodigo(), catalogoModel.getFoto())
        );

        try {
            if (imagenPrenda != null && !imagenPrenda.equals("")) {
                Picasso.get().load(imagenPrenda).into(holder.imgFotoPrenda);
            }
        } catch (Exception e) {
            Log.d("Exeption", "e: " + e);
        }
    }

    @Override
    public int getItemCount() {
        return catalogoModelList.size();
    }

    public class ViewHolderCatalogoAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        public void setOnClickListener() {
            btnEditarPrenda.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            SharedPreferences preferences = context.getSharedPreferences("Datos", MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("precio1", this.precio1);
            editor.putString("precio2", this.precio2);
            editor.putString("codigo", this.codigo);
            editor.putString("foto", this.foto);
            editor.putString("categoria", this.categoria);
            editor.commit();

            Intent intent = new Intent(context, EditorDeItems.class);
            context.startActivity(intent);
        }
    }

    private void descargarImagen(Context context, String nombre, String url) {
        if (url == null || url.trim().isEmpty()) {
            Toast.makeText(context, "La imagen no tiene una URL valida", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombreArchivo = nombre + "_" + System.currentTimeMillis() + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CatalogoCarol");

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri == null) {
            Toast.makeText(context, "No se pudo acceder al almacenamiento", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                Toast.makeText(context, "No se pudo abrir el archivo de salida", Toast.LENGTH_SHORT).show();
                return;
            }

            Handler mainHandler = new Handler(Looper.getMainLooper());

            new Thread(() -> {
                try (InputStream inputStream = new URL(url).openStream()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap == null) {
                        throw new IOException("No se pudo decodificar la imagen");
                    }

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();

                    mainHandler.post(() ->
                            Toast.makeText(context, "Imagen guardada en la galeria", Toast.LENGTH_SHORT).show()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        outputStream.close();
                    } catch (IOException ignored) {
                    }
                    mainHandler.post(() ->
                            Toast.makeText(context, "Error al descargar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al abrir flujo de salida", Toast.LENGTH_SHORT).show();
        }
    }
}
