package com.damon.ventadiamante.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.R;

public class VentaViewHolder extends RecyclerView.ViewHolder{

    public ImageView img_diamante,img_selected;
    public TextView name_vendedor,fecha_venta,descrip_diamantes,valor_venta,descripcion,respuesta_user;
    public ImageView viewCOlor,more_actions;
    public View layout_parent;

    public LinearLayout linearLayout;

    public VentaViewHolder(@NonNull View itemView) {
        super(itemView);

        img_diamante = itemView.findViewById(R.id.image_diamante);
        name_vendedor = itemView.findViewById(R.id.nombre_vendedor);
        fecha_venta = itemView.findViewById(R.id.fecha_venta);
        descrip_diamantes = itemView.findViewById(R.id.diamantes);
        valor_venta = itemView.findViewById(R.id.valor_venta);
        descripcion = itemView.findViewById(R.id.description);
        respuesta_user  = itemView.findViewById(R.id.respuesta_user);
        viewCOlor = itemView.findViewById(R.id.vista_color);
        layout_parent = (View) itemView.findViewById(R.id.vista_parent);
        img_selected = itemView.findViewById(R.id.img_selected);
        more_actions = itemView.findViewById(R.id.more_actions);

        linearLayout =itemView.findViewById(R.id.contenedor);
    }
}
