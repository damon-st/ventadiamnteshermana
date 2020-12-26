package com.damon.ventadiamante.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.viewholder.FotosFacturaViewHolder;

import java.util.ArrayList;

public  class AdapterFacturas extends RecyclerView.Adapter<FotosFacturaViewHolder> {

    ArrayList<Uri> fotosList = new ArrayList<>();
    Context context;

    public AdapterFacturas(ArrayList<Uri> fotosList, Context context) {
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
      // holder.setZoom();
        holder.setImage(fotosList.get(position),context);
        holder.fotoFactura.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.setZoom();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return fotosList.size();
    }
}
