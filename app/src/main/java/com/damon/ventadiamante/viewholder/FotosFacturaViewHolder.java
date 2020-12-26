package com.damon.ventadiamante.viewholder;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.damon.ventadiamante.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FotosFacturaViewHolder  extends RecyclerView.ViewHolder {

    public ImageView fotoFactura,deleteImage;
    private PhotoViewAttacher photoView;


    public FotosFacturaViewHolder(@NonNull View itemView) {
        super(itemView);

        deleteImage = itemView.findViewById(R.id.delete_img_selected);
        fotoFactura = itemView.findViewById(R.id.foto_factura);
    }

    public void setImage(Uri url, Context context){
        Glide.with(context).load(url).into(fotoFactura);
    }

    public void setZoom(){
        photoView = new PhotoViewAttacher(fotoFactura);
    }
}
