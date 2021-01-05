package com.damon.ventadiamante.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.R;

public class TextTimeHolder  extends RecyclerView.ViewHolder {

    public TextView txt_time;
    public ConstraintLayout constraintLayout;
    public TextTimeHolder(@NonNull View itemView) {
        super(itemView);
        txt_time =itemView.findViewById(R.id.text_time);
        constraintLayout = itemView.findViewById(R.id.fecha_item);

    }
}
