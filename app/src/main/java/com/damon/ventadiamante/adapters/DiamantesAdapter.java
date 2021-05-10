package com.damon.ventadiamante.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.activitys.CrearVentaActivity;
import com.damon.ventadiamante.interfaces.DiamanteClick;
import com.damon.ventadiamante.models.Diamante;
import com.damon.ventadiamante.models.Venta;
import com.damon.ventadiamante.viewholder.DiamantesViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiamantesAdapter  extends RecyclerView.Adapter<DiamantesViewHolder> {

    Context context;
    private int selectedStarPosition = -1;

    private boolean isChecked;
    List<Diamante> diamanteList = new ArrayList<>();
    DiamanteClick diamanteClick;
    private ViewPager2 viewPager2;

    private DatabaseReference priceRef;
    private boolean comprobar;

    public DiamantesAdapter(Context context,DiamanteClick diamanteClick,List<Diamante> diamanteList,ViewPager2 viewPager2,DatabaseReference priceRef) {
        this.context = context;
        this.diamanteClick = diamanteClick;
        this.diamanteList = diamanteList;
        this.viewPager2 = viewPager2;
        this.priceRef = priceRef;
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
        holder.imageDiamante.setImageResource(R.drawable.diamantes_free);
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

        holder.edit_price.setOnClickListener(view -> {
            editPrice(diamante,position);
        });

        holder.delete_price.setOnClickListener(view -> {
            AlertDialog.Builder alert  = new AlertDialog.Builder(context);
            alert.setTitle("Eliminar Este precio");
            alert.setMessage("Estas seguro que deseas eliminar no se podra recuperar?");
            alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deletePrice(diamante.getId(),dialogInterface,position);
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();

        });

//        if (position == diamanteList.size()-2){
//            viewPager2.post(runnable);
//        }

        if (!comprobar)
        PonerEldiamanteVendido(holder,position);
    }

    private void deletePrice(String id, DialogInterface dialogInterface,int position) {
        priceRef.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notifyItemRemoved(position);
                dialogInterface.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error al eliminar el precio" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editPrice(Diamante diamante, int position){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_respuesta);

        final EditText inpuTitle = dialog.findViewById(R.id.inputURL);
        final EditText inputNumber = dialog.findViewById(R.id.nuber);
        TextView title = dialog.findViewById(R.id.titulo_dialogo);
        inputNumber.setVisibility(View.VISIBLE);
        title.setText("Escribe el nuevo valor porfavor");
        inpuTitle.setText(diamante.getDiamantes());
        inputNumber.setText(""+diamante.getValor());

        TextView btn_cancel  = dialog.findViewById(R.id.textCancel);
        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        TextView btn_confirm = dialog.findViewById(R.id.textAdd);
        btn_confirm.setOnClickListener(view -> {
            HashMap<String,Object> editPrice = new HashMap<>();
            editPrice.put("diamantes",inpuTitle.getText().toString());
            editPrice.put("valor",Double.parseDouble(inputNumber.getText().toString()));
            priceRef.child(diamante.getId()).updateChildren(editPrice).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    diamante.setDiamantes(inpuTitle.getText().toString());
                    diamante.setValor(Double.parseDouble(inpuTitle.getText().toString()));
                    notifyItemChanged(position,diamante);
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Error al actualizar el precio "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
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


//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            diamanteList.addAll(diamanteList);
//            notifyDataSetChanged();
//        }
//    };
}
