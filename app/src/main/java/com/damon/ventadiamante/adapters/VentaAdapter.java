package com.damon.ventadiamante.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.activitys.CrearVentaActivity;
import com.damon.ventadiamante.activitys.FacturaWebView;
import com.damon.ventadiamante.activitys.ImageViewer;
import com.damon.ventadiamante.interfaces.ItemTouchHelperAdapter;
import com.damon.ventadiamante.interfaces.VentaClick;
import com.damon.ventadiamante.interfaces.VentaSingleClick;
import com.damon.ventadiamante.models.ImagesDB;
import com.damon.ventadiamante.models.TimeTextM;
import com.damon.ventadiamante.models.Venta;
import com.damon.ventadiamante.models.VentaPrincipal;
import com.damon.ventadiamante.viewholder.MyItemTouchHelper;
import com.damon.ventadiamante.viewholder.TextTimeHolder;
import com.damon.ventadiamante.viewholder.VentaViewHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

public class VentaAdapter  extends RecyclerView.Adapter<VentaViewHolder>
    implements ItemTouchHelperAdapter, StickyRecyclerHeadersAdapter<TextTimeHolder> {

    Activity context;
    List<Venta> ventaList;
    Timer timer;
    List<Venta> ventaSource;
    VentaClick ventaClick;
    VentaSingleClick ventaSingleClick;

    private boolean isEquals = false;
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    private DatabaseReference reference;
    private FirebaseStorage storage;


    private ItemTouchHelper mTouchHelper;

    public void setTouchHelper(ItemTouchHelper touchHelper){
        this.mTouchHelper = touchHelper;
    }


    public void setVentaClick(VentaClick ventaClick){
        this.ventaClick = ventaClick;
    }



    public VentaAdapter(Activity context, List<Venta> ventaList,VentaSingleClick ventaSingleClick) {
        this.context = context;
        this.ventaList = ventaList;
        ventaSource = ventaList;
        this.ventaSingleClick = ventaSingleClick;
        this.selected_items = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        reference = FirebaseDatabase.getInstance().getReference("Venta");
        storage = FirebaseStorage.getInstance();
        view =LayoutInflater.from(context).inflate(R.layout.new_venta_diamante,parent,false);
        return new VentaViewHolder(view,mTouchHelper);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull VentaViewHolder holder, int position) {

        Venta venta = ventaList.get(position);
            //animacion img

            //animation container

           // ((VentaViewHolder)holder).linearLayout.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));


        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                holder.img_diamante.setImageResource(R.drawable.diamantes_free);
            }
        });
        holder.name_vendedor.setText(venta.getVendedorName());

        SharedPreferences preferences = context.getPreferences(Context.MODE_PRIVATE);
        String tema = preferences.getString("tema","nuevo");

        if (tema.equals("antiguo")){
            holder.constraintTime.setVisibility(View.GONE);
            holder.fecha_venta.setVisibility(View.VISIBLE);
            holder.fecha_venta.setText(venta.getFechaVenta());
        }else {
           // holder.img_diamante.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition));
            holder.fecha_venta.setVisibility(View.GONE);
            holder.constraintTime.setVisibility(View.GONE);
            holder.time_new.setText(venta.getFechaVenta());
        }

        // ((VentaViewHolder)holder).fecha_venta.setText(venta.getFechaVenta());
        holder.time_new.setText(venta.getFechaVenta());


        holder.descrip_diamantes.setText(venta.getDescripcionDiamantes());
        holder.valor_venta.setText("$"+ venta.getPrecioDiamante());
        holder.descripcion.setText(venta.getDescripcion());

        holder.descripcion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData data = ClipData.newPlainText("",venta.getDescripcion());
                    clipboardManager.setPrimaryClip(data);
                    Toast.makeText(context, "Se a copiado descripcion", Toast.LENGTH_SHORT).show();
                }
            });

            if (venta.getColorValorPorVenta().equals(""))
                holder.respuesta_user.setVisibility(View.GONE);
            else
                holder.respuesta_user.setVisibility(View.VISIBLE);
        holder.respuesta_user.setText(venta.getColorValorPorVenta());
//        System.out.println("venta "+getTotal(venta));

            if (venta.getColorValorPorVenta().equals(""))
                holder.viewCOlor.setBackgroundResource(R.drawable.background_dialog);
            else
                holder.viewCOlor.setBackgroundResource(R.drawable.background_contesta);


        holder.layout_parent.setActivated(selected_items.get(position,false));

        holder.more_actions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(context,v);
                    popupMenu.inflate(R.menu.menu_options);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.respues_item){
                                ventaSingleClick.onCLickDiamante(venta,position);
                                popupMenu.dismiss();
                            }else if (item.getItemId() == R.id.editar_item){
                                Bundle bundle  =new Bundle();
                                bundle.putSerializable("pid",venta);
                                Intent intent = new Intent(context, CrearVentaActivity.class);
                                // intent.putExtra("pid",venta.getIdVentaRef());
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            }else if (item.getItemId() == R.id.delete_item){
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("Estas Seguro de eliminar ?");
                                alert.setMessage("Si eliminas no se podra recuperar.");
                                alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeVenta(position,venta.getIdVentaRef(),venta.getImage());
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();

                            }else if (item.getItemId() == R.id.imagenes_item){
                                mostraImagenes(venta.getImage(),venta.getIdVentaRef());
                            }else if (item.getItemId() == R.id.quitar_anotado){
                                quitarAnotado(venta.getIdVentaRef(),position);
                            }else if (item.getItemId() == R.id.descargar_factura){
//                                Intent intent = new Intent(context, FacturaWebView.class);
//                                intent.putExtra("id",venta.getIdVentaRef());
//                                context.startActivity(intent);

                                Uri uri = Uri.parse("https://ventadiamantes-329aa.firebaseapp.com/factura/"+venta.getIdVentaRef());
                                Log.d("url", " " + uri);
                                Intent intent1  = new Intent(Intent.ACTION_VIEW,uri);
                                context.startActivity(intent1);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ventaClick == null) return;
                    ventaClick.onCLickDiamante(venta,position);
                }
            });
        holder.layout_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (ventaClick == null) return false;
                    ventaClick.onLongClickDiamante(venta,position);
                    return true;
                }
            });


            toggleCheckedIcon(holder,position);

            if (venta.isCancel()){
                holder.linearLayout.setAlpha(0.3f);
            }else {
                holder.linearLayout.setAlpha(1);

            }

    }


    public void hastaAquiPagado(){
        if (ventaList.size()>0 || ventaList != null) {

            int posicion = -1;

            for (int i =0; i<ventaList.size();i++){

                if (ventaList.get(i).getColorValorPorVenta().trim().toLowerCase().contains("hasta")){
                    posicion = i;
                }
            }

            Log.d("pagado", "position: "+ posicion );

            for (int i = posicion ; i >=0; i--){
                Log.d("entra", "entro " + posicion);
                ventaList.get(i).setCancel(true);

            }
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            },500);
        }
    }

    public double getNewTotal(){
        double valor = 0;

        if (ventaList.size()>0){
            for (int i = 0; i< ventaList.size(); i++){
                if (!ventaList.get(i).isCancel()){
                    valor+= ventaList.get(i).getPrecioDiamante();
                }
            }
        }

        return valor;
    };


    private void quitarAnotado(String idVentaRef,int position) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("colorValorPorVenta","");
        reference.child(idVentaRef).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notifyItemChanged(position);
            }
        });

        hastaAquiPagado();
    }


    private void mostraImagenes(List<ImagesDB> image, String idVentaRef) {
        ArrayList<String> img = new ArrayList<>(image.size());
        ArrayList<String> ref = new ArrayList<>(image.size());

        for (ImagesDB images : image) {
            img.add(images.getImg());
            ref.add(images.getRef());
        }
        Intent intent  = new Intent(context, ImageViewer.class);
        intent.putStringArrayListExtra("path",img);
        intent.putStringArrayListExtra("ref",ref);
        intent.putExtra("db",idVentaRef);

        context.startActivity(intent);
    }

    private void removeVenta(int position, String pid, List<ImagesDB> image){
        if (image.size() > 0){
            for (ImagesDB  url : image){
                final StorageReference ref = storage.getReferenceFromUrl(url.getImg());
                ref.delete();
            }
        }

        reference.child(pid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ventaList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    private void toggleCheckedIcon(VentaViewHolder holder, int position) {
        if (selected_items.get(position,false)){
            holder.img_selected.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        }else {
            holder.img_selected.setVisibility(View.GONE);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }


    public void toggleSelection(int pos){
        current_selected_idx = pos;
        if (selected_items.get(pos,false)){
            selected_items.delete(pos);
        }else{
            selected_items.put(pos,true);
        }
        notifyItemChanged(pos);
    }

    public int getSelectedItemCount(){
        return selected_items.size();
    }

    public List<Integer> getSelectItms(){
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i =0; i < selected_items.size(); i++){
            items.add(selected_items.keyAt(i));
        }
        return items;
    }

    public String dbRef(int pos){
        return ventaList.get(pos).getIdVentaRef();
    }

    public List<String> pathImg(int pos){
        List<String> paths = new ArrayList<>();
        for (ImagesDB s : ventaList.get(pos).getImage()) {
            paths.add(s.getImg());
        }
        return  paths;
    }

    @Override
    public int getItemViewType(int position) {
        return 2;
    }

    public void clearSelections(){
        selected_items.clear();
        notifyDataSetChanged();
    }

    private void resetCurrentIndex() {
        current_selected_idx =-1;
    }


    public void deleteVenta(int position){
        ventaList.remove(position);
        notifyDataSetChanged();
    }

    double valor;
    public double getTotal(Venta venta){
        valor = valor+venta.getPrecioDiamante();
//       total = total + Double.valueOf(df.format(venta.getPrecioDiamante()));

        double res = valor;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
         valor = valor + venta.getPrecioDiamante();
        nf.format(res);
        return res;
    }

    public Venta getVentaList(int postion) {
        return ventaList.get(postion);
    }

    @Override
    public long getHeaderId(int position) {
        return ventaList.get(position).getNumeroVenta();
    }

    @Override
    public TextTimeHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_header,parent,false);
        return new TextTimeHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(TextTimeHolder textTimeHolder, int i) {
        textTimeHolder.txt_time.setText(ventaList.get(i).getFechaVenta());
    }

    @Override
    public int getItemCount() {
        if (ventaList.size()>0){
            return ventaList.size();
        }else {
            return 0;
        }
    }

    public double valor(){
        return valor;
    }


    public void searchVenta(final String searchKeywoard,boolean buscadeMulti,long desde,long hasta){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeywoard.trim().isEmpty()){
                    ventaList = ventaSource;
                }else {
                    ArrayList<Venta> temp = new ArrayList<>();
                    for (Venta venta: ventaSource){
                        if (buscadeMulti){
                            if (venta.getNumeroVenta() >= desde && venta.getNumeroVenta() <= hasta){
                                temp.add(venta);
                                valor += venta.getPrecioDiamante();
                            }
                        }else {
                            if (venta.getFechaVenta().toLowerCase().contains(searchKeywoard.toLowerCase())||venta.getDescripcion().toLowerCase().contains(searchKeywoard.toLowerCase())){
                                temp.add(venta);
                                valor += venta.getPrecioDiamante();
                            }
                        }

                    }
                    ventaList = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },500);
    }



    public void cancelTimer(){
        if (timer!=null){
            timer.cancel();
            valor=0;
        }
    }

    @Override
    public void onItemMove(int fromPositon, int toPosition) {
        Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);
        Venta venta = ventaList.get(fromPositon);
        ventaList.remove(venta);
        ventaList.add(toPosition,venta);
        notifyItemMoved(fromPositon,toPosition);

    }

    @Override
    public void onItemSwiped(int position,int direccion) {
        Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        System.out.println("position " + position);

        if (ventaList.get(position) != null){
            if (direccion == 16){

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Eliminar esta venta");
                alertDialog.setMessage("Estas seguro de eliminar esta venta nose podra recuperar nunca jamas ");
                alertDialog.setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeVenta(position,ventaList.get(position).getIdVentaRef(),ventaList.get(position).getImage());
                        dialog.dismiss();
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }else if (direccion == 32){

                System.out.println("Nombre comentario "  + getNombreComentario());

                String comentario =  getNombreComentario() + " dijo " + "\n "+ " anotado \uD83D\uDE03";

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("colorValorPorVenta",comentario);

                reference.child(ventaList.get(position).getIdVentaRef()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        try {
                          //  notifyItemChanged(position);
                        }catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                });

            }
        }
    }

    String nombreComentario;

    public void setNombreComentario(String nombreComentario) {
        this.nombreComentario = nombreComentario;
    }

    private String getNombreComentario(){
        return nombreComentario;
    }
}
