package com.damon.ventadiamante.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.activitys.ImageViewer;
import com.damon.ventadiamante.R;
import com.damon.ventadiamante.viewholder.FotosFacturaViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FotosFacturaAdapters  extends RecyclerView.Adapter<FotosFacturaViewHolder> {

    ArrayList<Uri> fotosList = new ArrayList<>();
    Context context;

    public FotosFacturaAdapters(ArrayList<Uri> fotosList , Context context){
        this.fotosList = fotosList;
        this.context = context;
    }


    @NonNull
    @Override
    public FotosFacturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_image,parent,false);
        return  new FotosFacturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotosFacturaViewHolder holder, int position) {
        Picasso.get().load(fotosList.get(position))
                .resize(400,400)
                .into(holder.fotoFactura);
       // holder.fotoFactura.setImageURI(fotosList.get(position));
       // holder.setImage(fotosList.get(position),context);
        holder.deleteImage.setVisibility(View.VISIBLE);

        holder.deleteImage.setOnClickListener(v -> deleteImage(position));

        holder.fotoFactura.setOnClickListener(v -> sentImageViewer());
    }

    private void sentImageViewer() {
        ArrayList<String > data = new ArrayList<>();
        for (Uri path : fotosList){
            data.add(path.toString());
        }
        Intent intent = new Intent(context, ImageViewer.class);
        intent.putStringArrayListExtra("path",data);
        context.startActivity(intent);
    }

    private void deleteImage(int position) {
        fotosList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fotosList.size();
    }
}
