package com.damon.ventadiamante.viewholder;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.models.Diamante;

public class DiamantesViewHolder extends RecyclerView.ViewHolder {


    public ImageView imageDiamante;
    public TextView diamantes,precioDiamante;
    public RadioButton radioSelect;
    public ImageView viewColor,delete_price,edit_price;

    public DiamantesViewHolder(@NonNull View itemView) {
        super(itemView);

        imageDiamante = itemView.findViewById(R.id.image_diamante);
        diamantes = itemView.findViewById(R.id.diamantes);
        precioDiamante = itemView.findViewById(R.id.valor_venta);
        radioSelect = itemView.findViewById(R.id.radio_select);
        viewColor = itemView.findViewById(R.id.vista_color);
        edit_price = itemView.findViewById(R.id.edit_price);
        delete_price = itemView.findViewById(R.id.delete_price);
    }

    public void setViewColor(Diamante diamante){
        GradientDrawable gradientDrawable = (GradientDrawable) viewColor.getBackground();
        if (TextUtils.isEmpty(diamante.getColorPrice())){
            viewColor.setVisibility(View.GONE);
        }else {
           // gradientDrawable.setColor(Color.parseColor(diamante.getColorPrice()));
            switch (diamante.getColorPrice()){
                case "#FDBE3B":
                    viewColor.setImageResource(R.drawable.background_venta);
                    break;
                case "#FF4842":
                    viewColor.setImageResource(R.drawable.background_contesta);
                    break;
                default:
                    viewColor.setImageResource(R.drawable.background_dialog);
                    break;
            }
        }
    }

}
