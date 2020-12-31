package com.damon.ventadiamante.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.models.ImagesDB;
import com.damon.ventadiamante.viewholder.FotosFacturaViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.transition.Hold;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public  class AdapterFacturas extends RecyclerView.Adapter<FotosFacturaViewHolder> {

    ArrayList<String> fotosList = new ArrayList<>();
    ArrayList<String> imagesDBS = new ArrayList<>();
    Context context;

    FirebaseStorage storage;
    FirebaseDatabase reference;

    String uidVenta;

    public AdapterFacturas(ArrayList<String> fotosList, ArrayList<String> imagesDBS, String data, Context context) {
        this.fotosList = fotosList;
        this.imagesDBS = imagesDBS;
        this.uidVenta = data;
        this.context = context;
    }

    @NonNull
    @Override
    public FotosFacturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_image,parent,false);
        storage  = FirebaseStorage.getInstance();
        reference = FirebaseDatabase.getInstance();
        return  new FotosFacturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotosFacturaViewHolder holder, int position) {
      // holder.setZoom()

        holder.deleteImage.setVisibility(View.VISIBLE);
        Picasso.get().load(fotosList.get(position)).into(holder.fotoFactura);
       // holder.setImage(Uri.parse(imgs.getImg()),context);
        holder.fotoFactura.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.setZoom();
                return true;
            }
        });

        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogDelete = new AlertDialog.Builder(context);
                dialogDelete.setTitle("Eliminar imagen");
                dialogDelete.setMessage("Estas seguro de eliminar esta imagen no se podra recuperar");
                dialogDelete.setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImageDB(fotosList.get(position),imagesDBS.get(position),position);
                        dialog.dismiss();
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

            }
        });
    }

    void deleteImageDB(String urlImage, String refDB, int position){
        DatabaseReference ref  =  reference.getReference("Venta").child(uidVenta);
        StorageReference storageReference = storage.getReferenceFromUrl(urlImage);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ref.child("image").child(refDB).removeValue();
                fotosList.remove(position);
                imagesDBS.remove(position);
                notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error al eliminar" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return fotosList.size();
    }
}
