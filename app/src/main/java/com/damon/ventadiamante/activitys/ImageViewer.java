package com.damon.ventadiamante.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.damon.ventadiamante.R;
import com.damon.ventadiamante.adapters.AdapterFacturas;
import com.damon.ventadiamante.adapters.FotosFacturaAdapters;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewer extends AppCompatActivity {


    private ImageView imageGrande;
    private PhotoViewAttacher photoView;
    private String path;

    RecyclerView recyclerFotos;
    AdapterFacturas fotosFacturaAdapters;

    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<Uri> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);


        recyclerFotos = findViewById(R.id.imagenes_facutra);
        recyclerFotos.setHasFixedSize(true);
        recyclerFotos.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        images = getIntent().getStringArrayListExtra("path");
        for(String paths : images){
            data.add(Uri.parse(paths));
        }
        //photoView = new PhotoViewAttacher(imageGrande);

        fotosFacturaAdapters = new AdapterFacturas(data,this);
        recyclerFotos.setAdapter(fotosFacturaAdapters);

       // Glide.with(this).load(path).into(imageGrande);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}