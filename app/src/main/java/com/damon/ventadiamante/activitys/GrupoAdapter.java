package com.damon.ventadiamante.activitys;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.models.Diamante;
import com.damon.ventadiamante.viewholder.GrupoHolder;

import java.util.ArrayList;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoHolder> {

    private ArrayList<Diamante> diamantes = new ArrayList<>();
    private Activity activity;


    public GrupoAdapter(ArrayList<Diamante> diamantes, Activity activity) {
        this.diamantes = diamantes;
        this.activity = activity;
    }

    @NonNull
    @Override
    public GrupoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.grupo_diamante,parent,false);
        return new GrupoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoHolder holder, int position) {
        holder.imageDiamnte.setImageResource(diamantes.get(position).getPath());
        holder.descripcion.setText(diamantes.get(position).getDiamantes());
        holder.precio.setText("$"+diamantes.get(position).getValor());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        holder.recyclerView.setHasFixedSize(true);
    }

    @Override
    public int getItemCount() {
        return diamantes.size();
    }
}
