package com.damon.ventadiamante.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.interfaces.DiamanteClick;
import com.damon.ventadiamante.models.Diamante;
import com.damon.ventadiamante.models.Venta;
import com.damon.ventadiamante.viewholder.DiamantesViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DiamantesAdapter  extends RecyclerView.Adapter<DiamantesViewHolder> {

    Context context;
    private int selectedStarPosition = -1;

    private boolean isChecked;
    List<Diamante> diamanteList = new ArrayList<>();
    DiamanteClick diamanteClick;
    private ViewPager2 viewPager2;

    private boolean comprobar;

    public DiamantesAdapter(Context context,DiamanteClick diamanteClick,List<Diamante> diamanteList,ViewPager2 viewPager2) {
        this.context = context;
        this.diamanteClick = diamanteClick;
        this.diamanteList = diamanteList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public DiamantesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diamantes_item,parent,false);
        return new DiamantesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiamantesViewHolder holder, final int position) {
        final Diamante diamante = diamanteList.get(position);
        holder.setViewColor(diamante);
        holder.imageDiamante.setImageResource(diamante.getPath());
        holder.diamantes.setText(diamante.getDiamantes());
        holder.precioDiamante.setText("$ " +diamante.getValor());

        holder.radioSelect.setChecked(position == selectedStarPosition);
        holder.radioSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedStarPosition = position;
                notifyItemRangeChanged(0,diamanteList.size());
                diamanteClick.onCLickDiamante(diamante,position);

            }
        });

        if (position == diamanteList.size()-2){
            viewPager2.post(runnable);
        }

        if (!comprobar)
        PonerEldiamanteVendido(holder,position);
    }

    public void PonerEldiamanteVendido(DiamantesViewHolder holder,int position){
        if (getValor()>0){
            for (int i = 0; i< diamanteList.size() ; i++){
                if (diamanteList.get(i).getValor() == getValor()&& i == position){
                    holder.radioSelect.setChecked(true);
                    comprobar = true;
                    break;
                }
            }
            System.out.println("A terminado");
        }


    }
    private double valor;
    public void setValor(double valor){
        this.valor = valor;
    }
    private double getValor(){
        return this.valor;
    }

    @Override
    public int getItemCount() {
        return diamanteList.size();
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            diamanteList.addAll(diamanteList);
            notifyDataSetChanged();
        }
    };
}
