package com.damon.ventadiamante.viewholder;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.R;

public class VentaViewHolder extends RecyclerView.ViewHolder implements
        GestureDetector.OnGestureListener,View.OnTouchListener{

    public ImageView img_diamante,img_selected;
    public TextView name_vendedor,fecha_venta,descrip_diamantes,valor_venta,descripcion,respuesta_user,time_new;
    public ImageView viewCOlor,more_actions;
    public View layout_parent;
    public ConstraintLayout constraintTime;

    public LinearLayout linearLayout;

    GestureDetector mGestureDetector;

    ItemTouchHelper touchHelper;

    public VentaViewHolder(@NonNull View itemView, ItemTouchHelper touchHelper) {
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
        time_new = itemView.findViewById(R.id.text_time);

        linearLayout =itemView.findViewById(R.id.contenedor);
        constraintTime = itemView.findViewById(R.id.fecha_item);

        this.touchHelper = touchHelper;
        mGestureDetector = new GestureDetector(itemView.getContext(),this);
        itemView.setOnTouchListener(this);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        touchHelper.startDrag(this);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }
}
