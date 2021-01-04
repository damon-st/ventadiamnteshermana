package com.damon.ventadiamante.activitys;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.damon.ventadiamante.R;
import com.damon.ventadiamante.Tools;
import com.damon.ventadiamante.adapters.CustomBottomSheet;
import com.damon.ventadiamante.adapters.VentaAdapter;
import com.damon.ventadiamante.conexcion.CheckNetworkConnection;
import com.damon.ventadiamante.interfaces.BuscarClick;
import com.damon.ventadiamante.interfaces.VentaClick;
import com.damon.ventadiamante.interfaces.VentaSingleClick;
import com.damon.ventadiamante.models.Diamante;
import com.damon.ventadiamante.models.ImagesDB;
import com.damon.ventadiamante.models.Venta;
import com.damon.ventadiamante.notifications.APIService;
import com.damon.ventadiamante.notifications.Client;
import com.damon.ventadiamante.notifications.Data;
import com.damon.ventadiamante.notifications.MyResponse;
import com.damon.ventadiamante.notifications.Sender;
import com.damon.ventadiamante.notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements  BuscarClick, VentaSingleClick {

    private FirebaseAuth mAuth;
    private ImageView createVenta,delete_all,refresh_page,calendario_btn,clear_text,ordenar_porFecha;
    VentaAdapter ventaAdapter;
    List<Venta> ventaList = new ArrayList<>();
    RecyclerView ventaRecycler;
    private DatabaseReference referenceVenta,refUser;
//    FirebaseRecyclerOptions<Venta> options;
//    FirebaseRecyclerAdapter<Venta, VentaViewHolder> adapter;
    double total;
    TextView totalTV,nuevoTotalTV;
    boolean isUser;
    EditText inputSearch ;

    private List<String> listKeyDelete = new ArrayList<>();
    private int itemPost =0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private LinearLayoutManager linearLayoutManager;
    private  int mCurrentPage =1;
    public  static final int TOTAL_ITEMS_TO_LOAD = 10;
    boolean isScrolling;
    private ImageButton btn_scroll_down;

    int currentItems, totalItems, scrollOutitems;

    SwipeRefreshLayout swipeRefreshLayout;
    String uid,nombreComentario;

    private AlertDialog dialogAddComentario;

    APIService apiService;
    boolean notify;

    String  messageReciverID;
//    private FirebaseRecyclerPagingAdapter<Venta, VentaViewHolder> mAdapter;
    int index =5 ;

    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    private FirebaseStorage reference;

    private ArrayList<String> rutasImagenes = new ArrayList<>();


    private ProgressDialog progressDialog;




    Animation animationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        animationUtils = AnimationUtils.loadAnimation(this,R.anim.sacudir_btn);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        reference = FirebaseStorage.getInstance();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        createVenta = findViewById(R.id.imageAddNoteMain);

        btn_scroll_down = findViewById(R.id.ultimoItem);
        ordenar_porFecha = findViewById(R.id.imageOrderFecha);
        clear_text = findViewById(R.id.borrar_text);
        nuevoTotalTV = findViewById(R.id.nuevo_valor);
        calendario_btn = findViewById(R.id.calendari);
        refresh_page = findViewById(R.id.refresh_page);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        inputSearch = findViewById(R.id.inputSearch);
        totalTV = findViewById(R.id.imageAddNote);
        ventaRecycler = findViewById(R.id.venta_recyclerView);
        ventaRecycler.setHasFixedSize(true);


        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        ventaRecycler.setLayoutManager(linearLayoutManager);
        ventaRecycler.setVerticalScrollBarEnabled(true);

        delete_all = findViewById(R.id.delete_all);

        actionModeCallback = new ActionModeCallback();

        referenceVenta = FirebaseDatabase.getInstance().getReference().child("Venta");
        refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        createVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CrearVentaActivity.class);
                startActivity(intent);
            }
        });

        ventaAdapter = new VentaAdapter(this,ventaList,this);
        getTotalValor();


//        LayoutAnimationController controller  =AnimationUtils.loadLayoutAnimation(this,R.anim.fade_scale_animation);
//        ventaRecycler.setLayoutAnimation(controller);
//        ventaRecycler.scheduleLayoutAnimation();
        ventaRecycler.setAdapter(ventaAdapter);

       // getDataVenta();

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after>0){
                    ventaRecycler.setAdapter(ventaAdapter);
                    totalTV.setVisibility(View.GONE);
                    nuevoTotalTV.setVisibility(View.VISIBLE);
                    clear_text.setVisibility(View.VISIBLE);
                }else {
//                    ventaRecycler.setAdapter(mAdapter);
                    nuevoTotalTV.setVisibility(View.GONE);
                    totalTV.setVisibility(View.VISIBLE);
                    clear_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ventaAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ventaList.size() !=0){
                    ventaAdapter.searchVenta(s.toString(),false,0,0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nuevoValor();
                        }
                    },1000);
                }
            }
        });
        updateToken();

        getDataUser();

        swipeRefreshLayout.setRefreshing(false);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mCurrentPage++;
//                itemPost =0;
//                loadMoreVenta();
//            }
//        });

//        getAllVentas("");

        delete_all.setOnClickListener(v -> deleteVentaAll());

        refresh_page.setOnClickListener(v -> setRefresh_page());

        setData();

        clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSearch.setText("");
            }
        });

        ordenar_porFecha.setOnClickListener(v -> MostarDialog());

        ventaAdapter.setVentaClick(new VentaClick() {
            @Override
            public void onCLickDiamante(Venta venta, int position) {
                if (ventaAdapter.getSelectedItemCount() >0)
                    enableActionMode(position);
            }

            @Override
            public void onLongClickDiamante(Venta venta, int position) {
                    enableActionMode(position);
            }
        });

        ventaRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
                    //btn_scroll_down.setVisibility(View.GONE);
                    isScrolling = true;
                }else {
                    isScrolling = false;
                    //btn_scroll_down.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemcout  = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if ((pastVisibleItem+visibleItemcout >= totalItemCount) ){
                    btn_scroll_down.setVisibility(View.GONE);
                }else {
                    btn_scroll_down.setVisibility(View.VISIBLE);
                    btn_scroll_down.startAnimation(animationUtils);
                }

            }
        });

        btn_scroll_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventaRecycler.smoothScrollToPosition(ventaAdapter.getItemCount());
            }
        });

    }

    private void enableActionMode(int position) {
        if (actionMode == null){
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        ventaAdapter.toggleSelection(position);
        int count = ventaAdapter.getSelectedItemCount();
        if (count ==0){
            actionMode.finish();
        }else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private void setRefresh_page(){
//        mAdapter.refresh();
//        total = 0;
//        ventaList.clear();
//        getTotalValor();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                linearLayoutManager.smoothScrollToPosition(ventaRecycler,new RecyclerView.State(),ventaRecycler.getAdapter().getItemCount());
//                ventaRecycler.smoothScrollToPosition(ventaAdapter.getItemCount());
//            }
//        },1000);

        recreate();

    }

    private void deleteVentaAll(){

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Estas Seguro de Eliminar todas las ventas ??");
        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setTitle("Espera eliminando Porfavor.....");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                DeleteAll();
                dialog.dismiss();

            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });

        dialog.show();





    }

    void DeleteAll(){
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    EliminarImage(rutasImagenes);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                referenceVenta.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
//                        mAdapter.refresh();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    ventaList.clear();
                                    ventaAdapter.notifyDataSetChanged();
                                    totalTV.setText("TOTAL = ");
                                }
                            });
                        }
                    }
                });
            }
        }.start();
    }

    private void nuevoValor(){
        setTotalNuevo(ventaAdapter.valor());
       // linearLayoutManager.smoothScrollToPosition(ventaRecycler,new RecyclerView.State(),ventaRecycler.getAdapter().getItemCount());
    }

    private void setData(){
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
//                inputSearch.setText(dateFormatter.format(newDate.getTime()));
                inputSearch.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy ")
                        .format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        calendario_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTime.show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser firebaseUser = mAuth.getCurrentUser();
//        if (firebaseUser ==null){
//            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }else {
//            System.out.println("Vienvenido" + firebaseUser.getUid());
//            isUser = true;
//        }
//        mAdapter.startListening();
//        linearLayoutManager.smoothScrollToPosition(ventaRecycler,new RecyclerView.State(),ventaRecycler.getAdapter().getItemCount());
//        System.out.println("itemCOunt"+ index);
//        ventaRecycler.setScrollBarSize(10);
    }


    private void loadMoreVenta(){
        Query ventQuery = referenceVenta.orderByKey().endAt(mLastKey).limitToLast(10);
        ventQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
              Venta  venta = snapshot.getValue(Venta.class);
                String ventaKey =  snapshot.getKey();
                listKeyDelete.add(snapshot.getKey());
                if (!mPrevKey.equals(ventaKey)){
                    ventaList.add(itemPost++,venta);
//                    total = total+venta.getPrecioDiamante();
//
//                    setTotal(total);
                }else {
                    mPrevKey = mLastKey;
                }

                if (itemPost ==1){
                    mLastKey = ventaKey;
                }

                ventaAdapter.notifyDataSetChanged();


                swipeRefreshLayout.setRefreshing(false);

                linearLayoutManager.scrollToPositionWithOffset(10,0);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getAllVentas(String search){
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance(5)
//                .setPageSize(10)
//                .build();
//
//        DatabasePagingOptions<Venta> options = new DatabasePagingOptions.Builder<Venta>()
//                .setLifecycleOwner(this)
////                .setQuery(referenceVenta.orderByChild("fechaVenta").startAt(search).endAt(search+"\uf8ff"), config, Venta.class)
//                .setQuery(referenceVenta, config, Venta.class)
//                .build();
//
//        mAdapter = new FirebaseRecyclerPagingAdapter<Venta, VentaViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull VentaViewHolder holder, int i, @NonNull Venta venta) {
//                holder.img_diamante.setImageResource(R.drawable.diamantes_free);
//                holder.name_vendedor.setText(venta.getVendedorName());
//                holder.fecha_venta.setText(venta.getFechaVenta());
//                holder.descrip_diamantes.setText(venta.getDescripcionDiamantes());
//                holder.valor_venta.setText("$"+ venta.getPrecioDiamante());
//                holder.descripcion.setText(venta.getDescripcion());
//                holder.respuesta_user.setText(venta.getColorValorPorVenta());
////        System.out.println("venta "+getTotal(venta));
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onCLickDiamante(venta,i);
//                    }
//                });
//
//
//            }
//
//            @Override
//            protected void onLoadingStateChanged(@NonNull LoadingState state) {
//                switch (state){
//                    case LOADING_INITIAL:
//                    case LOADING_MORE:
//                        // Do your loading animation
//                        swipeRefreshLayout.setRefreshing(true);
//                        break;
//
//                    case LOADED:
//                        // stop animation
//                        swipeRefreshLayout.setRefreshing(false);
//                        break;
//
//                    case FINISHED:
//                        //Reached end of Data set
//                        swipeRefreshLayout.setRefreshing(false);
//                        break;
//
//                    case ERROR:
//                        retry();
//                        break;
//
//
//                }
//            }
//
//            @NonNull
//            @Override
//            public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.venta_diamante_main,parent,false);
//                return new VentaViewHolder(view);
//            }
//
//            @Override
//            protected void onError(@NonNull DatabaseError databaseError) {
//                super.onError(databaseError);
//                swipeRefreshLayout.setRefreshing(false);
//                mAdapter.stopListening();
//                databaseError.toException().printStackTrace();
//            }
//        };
//
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mAdapter.refresh();
//            }
//        });
//        ventaRecycler.setAdapter(mAdapter);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                linearLayoutManager.smoothScrollToPosition(ventaRecycler,new RecyclerView.State(),ventaRecycler.getAdapter().getItemCount());
//            }
//        },2000);
    }


    Query queryVeta;
    private void getDataVenta(){

//       referenceVenta.addValueEventListener(new ValueEventListener() {
//           @Override
//           public void onDataChange(@NonNull DataSnapshot snapshot) {
//               if (snapshot.exists()){
//                   for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                       Venta venta = dataSnapshot.getValue(Venta.class);
//                       ventaList.add(venta);
//                       ventaAdapter.notifyDataSetChanged();
//                   }
//               }
//           }
//
//           @Override
//           public void onCancelled(@NonNull DatabaseError error) {
//
//           }
//       });

//        options = new FirebaseRecyclerOptions.Builder<Venta>()
//                        .setQuery(referenceVenta,Venta.class)
//                        .build();
//
//        adapter = new FirebaseRecyclerAdapter<Venta, VentaViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull VentaViewHolder holder, int i, @NonNull Venta venta) {
//                        holder.img_diamante.setImageResource(R.drawable.diamantes_free);
//                        holder.name_vendedor.setText(venta.getVendedorName());
//                        holder.fecha_venta.setText(venta.getFechaVenta());
//                        holder.descrip_diamantes.setText(venta.getDescripcionDiamantes());
//                        holder.valor_venta.setText("$"+ venta.getPrecioDiamante());
//                        holder.descripcion.setText(venta.getDescripcion());
////                        total = total + venta.getPrecioDiamante();
//                        DecimalFormat df = new DecimalFormat("0.00");
//                        total = total + Double.valueOf(df.format(venta.getPrecioDiamante()));
//                        totalTV.setText("TOTAL = " + total);
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view  = LayoutInflater.from(getApplicationContext()).inflate(R.layout.venta_diamante_main,parent,false);
//                        return new VentaViewHolder(view);
//                    }
//                };
//
//
//        ventaRecycler.setAdapter(adapter);
//        adapter.startListening();


        queryVeta = referenceVenta.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        queryVeta.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
             Venta   venta = snapshot.getValue(Venta.class);
                listKeyDelete.add(snapshot.getKey());
                itemPost++;
                if (itemPost ==1){
                    String ventKey = snapshot.getKey();
                    mLastKey = ventKey;
                    mPrevKey = ventKey;

                }
                ventaList.add(venta);

                ventaAdapter.notifyDataSetChanged();
//                total = total+venta.getPrecioDiamante();
//                DecimalFormat df = new DecimalFormat("#.##");
////               total = total + Double.valueOf(df.format(venta.getPrecioDiamante()));
//
//               setTotal(total);

                ventaRecycler.smoothScrollToPosition(ventaRecycler.getAdapter().getItemCount());

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Venta venta = snapshot.getValue(Venta.class);
                String key = snapshot.getKey();
                int index = listKeyDelete.indexOf(key);
                try {
                    ventaList.set(index,venta);
                    ventaAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getTotalValor(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                referenceVenta.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        if (snapshot.exists()){

                            String vendedorName = "";
                            String vendedorUID = "";
                            String fechaVenta = "";
                            String  colorValorPorVenta ="";
                            String  colorVendedor ="";
                            String descripcionDiamantes = "";
                            double precioDiamante =0;
                            String descripcion = "";
                            long numeroVenta = 0;
                            String idVentaRef = "";
                            List<ImagesDB> image = new ArrayList<>();

                            if (snapshot.child("vendedorName").getValue() !=null)
                                vendedorName = snapshot.child("vendedorName").getValue().toString();
                            if (snapshot.child("vendedorUID").getValue() !=null)
                                vendedorUID = snapshot.child("vendedorUID").getValue().toString();

                            if (snapshot.child("colorValorPorVenta").getValue() !=null)
                                colorValorPorVenta = snapshot.child("colorValorPorVenta").getValue().toString();

                            if (snapshot.child("colorVendedor").getValue() !=null)
                                colorVendedor = snapshot.child("colorVendedor").getValue().toString();

                            if (snapshot.child("descripcionDiamantes").getValue() !=null)
                                descripcionDiamantes = snapshot.child("descripcionDiamantes").getValue().toString();

                            if (snapshot.child("precioDiamante").getValue() !=null)
                                precioDiamante =Double.parseDouble(snapshot.child("precioDiamante").getValue().toString());

                            if (snapshot.child("descripcion").getValue() !=null)
                                descripcion = snapshot.child("descripcion").getValue().toString();

                            if (snapshot.child("numeroVenta").getValue() !=null)
                                numeroVenta = Long.parseLong(snapshot.child("numeroVenta").getValue().toString()) ;

                            if (snapshot.child("idVentaRef").getValue() !=null)
                                idVentaRef = snapshot.child("idVentaRef").getValue().toString();

                            if (snapshot.child("fechaVenta").getValue() !=null)
                                fechaVenta = snapshot.child("fechaVenta").getValue().toString();

                            if (snapshot.child("image").getChildrenCount() >0){
                                for (DataSnapshot dataSnapshot: snapshot.child("image").getChildren()){
                                    image.add(new ImagesDB(dataSnapshot.getKey(),dataSnapshot.getValue().toString()));
                                    rutasImagenes.clear();
                                    rutasImagenes.add(dataSnapshot.getValue().toString());
                            }
                        }

//                        System.out.println(rutasImagenes);

                            Venta venta = new Venta(vendedorName,vendedorUID,fechaVenta,colorValorPorVenta,colorVendedor,
                                      descripcionDiamantes,precioDiamante,descripcion,numeroVenta,idVentaRef,image);
                            total = total+venta.getPrecioDiamante();
                            setTotal(total);
                            listKeyDelete.add(snapshot.getKey());
                            ventaList.add(venta);
                            ventaAdapter.notifyDataSetChanged();

                            ventaRecycler.smoothScrollToPosition(ventaAdapter.getItemCount());


                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        String vendedorName = "";
                        String vendedorUID = "";
                        String fechaVenta = "";
                        String  colorValorPorVenta ="";
                        String  colorVendedor ="";
                        String descripcionDiamantes = "";
                        double precioDiamante =0;
                        String descripcion = "";
                        long numeroVenta = 0;
                        String idVentaRef = "";
                        List<ImagesDB> image = new ArrayList<>();

                        if (snapshot.child("vendedorName").getValue() !=null)
                            vendedorName = snapshot.child("vendedorName").getValue().toString();
                        if (snapshot.child("vendedorUID").getValue() !=null)
                            vendedorUID = snapshot.child("vendedorUID").getValue().toString();

                        if (snapshot.child("colorValorPorVenta").getValue() !=null)
                            colorValorPorVenta = snapshot.child("colorValorPorVenta").getValue().toString();

                        if (snapshot.child("colorVendedor").getValue() !=null)
                            colorVendedor = snapshot.child("colorVendedor").getValue().toString();

                        if (snapshot.child("descripcionDiamantes").getValue() !=null)
                            descripcionDiamantes = snapshot.child("descripcionDiamantes").getValue().toString();

                        if (snapshot.child("precioDiamante").getValue() !=null)
                            precioDiamante =Double.parseDouble(snapshot.child("precioDiamante").getValue().toString());

                        if (snapshot.child("descripcion").getValue() !=null)
                            descripcion = snapshot.child("descripcion").getValue().toString();

                        if (snapshot.child("numeroVenta").getValue() !=null)
                            numeroVenta = Long.parseLong(snapshot.child("numeroVenta").getValue().toString()) ;

                        if (snapshot.child("idVentaRef").getValue() !=null)
                            idVentaRef = snapshot.child("idVentaRef").getValue().toString();

                        if (snapshot.child("fechaVenta").getValue() !=null)
                            fechaVenta = snapshot.child("fechaVenta").getValue().toString();

                        if (snapshot.child("image").getChildrenCount() >0){
                            for (DataSnapshot dataSnapshot: snapshot.child("image").getChildren()){
                                image.add(new ImagesDB(dataSnapshot.getKey(),dataSnapshot.getValue().toString()));
                                rutasImagenes.clear();
                                rutasImagenes.add(dataSnapshot.getValue().toString());
                            }
                        }

//                        System.out.println(rutasImagenes);

                        Venta venta = new Venta(vendedorName,vendedorUID,fechaVenta,colorValorPorVenta,colorVendedor,
                                descripcionDiamantes,precioDiamante,descripcion,numeroVenta,idVentaRef,image);
                        String key = snapshot.getKey();
                        int index = listKeyDelete.indexOf(key);
                        try {
                            ventaList.set(index,venta);
                            ventaAdapter.notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        String vendedorName = "";
                        String vendedorUID = "";
                        String fechaVenta = "";
                        String  colorValorPorVenta ="";
                        String  colorVendedor ="";
                        String descripcionDiamantes = "";
                        double precioDiamante =0;
                        String descripcion = "";
                        long numeroVenta = 0;
                        String idVentaRef = "";
                        List<ImagesDB> image = new ArrayList<>();

                        if (snapshot.child("vendedorName").getValue() !=null)
                            vendedorName = snapshot.child("vendedorName").getValue().toString();
                        if (snapshot.child("vendedorUID").getValue() !=null)
                            vendedorUID = snapshot.child("vendedorUID").getValue().toString();

                        if (snapshot.child("colorValorPorVenta").getValue() !=null)
                            colorValorPorVenta = snapshot.child("colorValorPorVenta").getValue().toString();

                        if (snapshot.child("colorVendedor").getValue() !=null)
                            colorVendedor = snapshot.child("colorVendedor").getValue().toString();

                        if (snapshot.child("descripcionDiamantes").getValue() !=null)
                            descripcionDiamantes = snapshot.child("descripcionDiamantes").getValue().toString();

                        if (snapshot.child("precioDiamante").getValue() !=null)
                            precioDiamante =Double.parseDouble(snapshot.child("precioDiamante").getValue().toString());

                        if (snapshot.child("descripcion").getValue() !=null)
                            descripcion = snapshot.child("descripcion").getValue().toString();

                        if (snapshot.child("numeroVenta").getValue() !=null)
                            numeroVenta = Long.parseLong(snapshot.child("numeroVenta").getValue().toString()) ;

                        if (snapshot.child("idVentaRef").getValue() !=null)
                            idVentaRef = snapshot.child("idVentaRef").getValue().toString();

                        if (snapshot.child("fechaVenta").getValue() !=null)
                            fechaVenta = snapshot.child("fechaVenta").getValue().toString();

                        if (snapshot.child("image").getChildrenCount() >0){
                            for (DataSnapshot dataSnapshot: snapshot.child("image").getChildren()){
                                image.add(new ImagesDB(dataSnapshot.getKey(),dataSnapshot.getValue().toString()));
                            }
                        }




                        Venta venta = new Venta(vendedorName,vendedorUID,fechaVenta,colorValorPorVenta,colorVendedor,
                                descripcionDiamantes,precioDiamante,descripcion,numeroVenta,idVentaRef,image);
                        String key = snapshot.getKey();
                        int index = listKeyDelete.indexOf(key);
                        try {
                            total = total-venta.getPrecioDiamante();
                            setTotal(total);
                            ventaAdapter.notifyItemRemoved(index);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Error database getValores" + error.getMessage());
                    }
                });
            }
        }.start();
    }

    private void updateToken(){

        new Thread(){
            @Override
            public void run() {
                super.run();

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()){
                            String token = task.getResult().toString();
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                            Token token1 = new Token(token);

                            reference.child(firebaseUser.getUid()).setValue(token1);
                        }
                    }
                });
            }
        }.start();


    }

    private void setTotal(double total){
        double res = total;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        totalTV.setText("TOTAL = " + nf.format(res));
    }

    private void setTotalNuevo(double total){
        double res = total;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        nuevoTotalTV.setText("TOTAL = " + nf.format(res));
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mAdapter !=null){
//            mAdapter.stopListening();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mAdapter!=null){
//            mAdapter.startListening();
//        }
    }

    @Override
    public void onCLickDiamante(Venta venta, int position) {
        showAddURLDialog(venta);
    }

    private void showAddURLDialog(Venta venta){
        if (dialogAddComentario == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_respuesta,
                            (ViewGroup)findViewById(R.id.layoutAddUrlContainer));
            builder.setView(view);

            dialogAddComentario = builder.create();
            if (dialogAddComentario.getWindow() !=null){
                dialogAddComentario.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();
            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inputURL.getText().toString().trim().isEmpty()){
                        Toast.makeText(MainActivity.this, "Escribe la respuesta", Toast.LENGTH_SHORT).show();
                    }else {
                        notify = true;
                        setComentario(venta,inputURL.getText().toString());
                        dialogAddComentario = null;
                    }
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddComentario.dismiss();
                    dialogAddComentario = null;
                }
            });

            if (venta.getImage() != null){
                view.findViewById(R.id.mostarImg).setVisibility(View.GONE);
                view.findViewById(R.id.mostarImg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ImageViewer.class);
                       // intent.putStringArrayListExtra("path", (ArrayList<String>) venta.getImage());
                        startActivity(intent);
                        dialogAddComentario.dismiss();
                        dialogAddComentario = null;
                    }
                });
            }

        }
        dialogAddComentario.setCanceledOnTouchOutside(false);
        dialogAddComentario.setCancelable(false);
        dialogAddComentario.show();
    }

    void setComentario(Venta venta,String comentario){
        String respuesta =  nombreComentario + " dijo " + "\n "+ comentario;
        messageReciverID = venta.getVendedorUID();
        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("colorValorPorVenta",respuesta);
        referenceVenta.child(venta.getIdVentaRef()).updateChildren(hashMap);


        final String msg = "\uD83D\uDE03" + comentario;


        if (notify) {
            System.out.println("si entra aqui" + notify);
            sendNotifiaction(messageReciverID, nombreComentario, msg);
        }
        notify = false;
        dialogAddComentario.dismiss();
        dialogAddComentario = null;
//        mAdapter.refresh();
//        ventaRecycler.smoothScrollToPosition(mAdapter.getItemCount());

    }

    void getDataUser(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            nombreComentario = snapshot.child("name").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }.start();

    }

    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Token token = snapshot.getValue(Token.class);
                            Data data = new Data(uid, R.mipmap.ic_launcher, username + ": " + message, "Respondio tu venta",
                                    receiver);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success != 1) {
                                                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                        }
                                    });
                        }
                    } catch (Exception e) {
                        System.out.println("Error" + e);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void MostarDialog(){
        CustomBottomSheet customBottomSheet = new CustomBottomSheet(this, this);
        customBottomSheet.setCancelable(true);
        customBottomSheet.show(getSupportFragmentManager(),customBottomSheet.getTag());
    }


    @Override
    public void onClickBuscar(String desde, String hasta, long desdeLong, long hastaLong) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String d = dateFormat.format(desdeLong);
        long fechaDesde = Long.parseLong(d.replace("-",""));
        String h = dateFormat.format(hastaLong);
        long fechaHasta = Long.parseLong(h.replace("-",""));
        System.out.println(fechaDesde);

        inputSearch.setText("");
        ventaAdapter.searchVenta(desde,true,fechaDesde,fechaHasta);
        ventaRecycler.setAdapter(ventaAdapter);
        totalTV.setVisibility(View.GONE);
        nuevoTotalTV.setVisibility(View.VISIBLE);
        clear_text.setVisibility(View.VISIBLE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nuevoValor();
                ventaAdapter.cancelTimer();
            }
        },1000);
    }

    private class ActionModeCallback implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Tools.setSystemBarColor(MainActivity.this,R.color.colorPrimaryDark);
            mode.getMenuInflater().inflate(R.menu.main_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.delete_venta){
                //codigo aqui
                eliminarVentas();
                mode.finish();
                return true;
            }else if (id == R.id.marcar_anotado_venta){
                marcarVentaAnotado();
                mode.finish();
                return true;
            }else if (id == R.id.seleccionartodo){
                selectedAll(mode);
                return true;
            }else if (id == R.id.calcularprecio){
                selectedCalculatePrice();

                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            ventaAdapter.clearSelections();
            actionMode = null;
            setTotal(total);
            Tools.setSystemBarColor(MainActivity.this,R.color.colorPrimary);
        }
    }

    private void selectedCalculatePrice() {
       double total=0;
        List<Integer> selectedItempost = ventaAdapter.getSelectItms();
        for (int i = selectedItempost.size()-1;i>=0; i--){
            total = total+ventaList.get(selectedItempost.get(i)).getPrecioDiamante();
        }
        setTotal(total);
    }

    private void selectedAll(ActionMode mode) {
        for (int i = 0; i< ventaList.size(); i++){
            enableActionMode(i);
        }
    }

    private void marcarVentaAnotado() {
        List<Integer> selectedItemPost = ventaAdapter.getSelectItms();
        for (int i = selectedItemPost.size()-1; i >=0 ; i--){
            marcarAnotado(ventaAdapter.dbRef(selectedItemPost.get(i)),ventaList.get(selectedItemPost.get(i)).getVendedorUID());
        }
    }

    private void marcarAnotado(String dbRef,String receiver) {
        String respuesta =  nombreComentario + " dijo " + "\n "+ " anotado \uD83D\uDE03";
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("colorValorPorVenta",respuesta);
        referenceVenta.child(dbRef).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendNotifiaction(receiver,nombreComentario,respuesta);
            }
        });

    }

    private void eliminarVentas() {
        List<Integer> selectedItemPost = ventaAdapter.getSelectItms();
//        ArrayList<String> data = new ArrayList<>();
        for (int i = selectedItemPost.size()-1; i >=0 ; i--){
            if (ventaAdapter.pathImg(selectedItemPost.get(i)) != null){
                try {
                    EliminarImage(ventaAdapter.pathImg(selectedItemPost.get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            deleteForDB(ventaAdapter.dbRef(selectedItemPost.get(i)));
            ventaAdapter.deleteVenta(selectedItemPost.get(i));
        }
    }

    private void EliminarImage(List<String> imgs) throws Exception{

        for(String img: imgs){
            final StorageReference ref = reference.getReferenceFromUrl(img);
            ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                    }else {
                        Toast.makeText(MainActivity.this, "Error al Eliminar la Imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void deleteForDB(String dbRef) {
        referenceVenta.child(dbRef).removeValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_all){
            deleteVentaAll();
            return true;
        }else if (id == R.id.refresh_page){
            setRefresh_page();
            return true;
        }else if (id == R.id.exitApp){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            deleteCache(this);
            Glide.get(this).clearMemory();
        }catch (Exception e){}


    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }



}