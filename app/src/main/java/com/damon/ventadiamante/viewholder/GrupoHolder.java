package com.damon.ventadiamante.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.R;

public class GrupoHolder  extends RecyclerView.ViewHolder {

    public ImageView imageDiamnte;
    public TextView precio, descripcion;

    public RecyclerView recyclerView;
    public GrupoHolder(@NonNull View itemView) {
        super(itemView);

        imageDiamnte = itemView.findViewById(R.id.image_diamante);
        descripcion = itemView.findViewById(R.id.description_diamante);
        precio = itemView.findViewById(R.id.precio_diamante);
        recyclerView = itemView.findViewById(R.id.recycler_diamantes);

    }
}
