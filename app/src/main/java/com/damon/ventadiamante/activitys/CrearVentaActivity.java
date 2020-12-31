package com.damon.ventadiamante.activitys;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.adapters.DiamantesAdapter;
import com.damon.ventadiamante.adapters.FotosFacturaAdapters;
import com.damon.ventadiamante.interfaces.DiamanteClick;
import com.damon.ventadiamante.models.Diamante;
import com.damon.ventadiamante.models.Users;
import com.damon.ventadiamante.notifications.APIService;
import com.damon.ventadiamante.notifications.Client;
import com.damon.ventadiamante.notifications.Data;
import com.damon.ventadiamante.notifications.MyResponse;
import com.damon.ventadiamante.notifications.Sender;
import com.damon.ventadiamante.notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.labters.documentscanner.ImageCropActivity;
import com.labters.documentscanner.helpers.ScannerConstants;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearVentaActivity extends AppCompatActivity implements DiamanteClick {

    ViewPager2 pager_diamantes;
    DiamantesAdapter diamantesAdapter;
    TextView diamante_text, valor_diamante, fecha_tv, name_user_tv;
    String diamante_texto = "", nombre_user = "", descripcion = "";
    double valor_diama;
    EditText descripcion_opcional;

    DatabaseReference reference, ventaRef, referenceUsers;
    FirebaseAuth auth;
    String vendedorUID;
    String colorVenta;
    Button vender;
    ProgressDialog progressDialog;

    APIService apiService;

    FirebaseUser fuser;

    boolean notify;
    String messageReciverID;

    List<Diamante> diamanteList = new ArrayList<>();

    ImageView addNewPago, calendarNewDate, btn_addFactura, imgFactura;


    AlertDialog alertDialog;
    long ventaFecha;


    StorageReference firebaseStorage, filePath;

    @NonNull
    Uri imgURI;

    Bitmap bitmap = null;

    private Handler sliderHandler = new Handler();


    private FotosFacturaAdapters fotosFacturaAdapters;
    private RecyclerView fotosRecycler;

    private ArrayList<Uri> fotosList = new ArrayList<>();

    private AlertDialog.Builder dialogErrores;

    private Dialog dialogProgress;
    private TextView textoProgress;

    private Dialog dialogoFoto;

    String ventId;

    boolean isUpdate;

    int cuentaFotosSubidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_venta);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                ActivityCompat.requestPermissions(CrearVentaActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
            }
        }

        dialogoFoto = new Dialog(this);
        dialogoFoto.setCanceledOnTouchOutside(true);
        dialogProgress = new Dialog(this);
        dialogErrores = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        setDiamanteList();

        firebaseStorage = FirebaseStorage.getInstance().getReference().child("Facturas");

        fotosRecycler = findViewById(R.id.factura_img_recycler);
        fotosRecycler.setHasFixedSize(true);
        fotosRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fotosFacturaAdapters = new FotosFacturaAdapters(fotosList, this);
        fotosRecycler.setAdapter(fotosFacturaAdapters);


        btn_addFactura = findViewById(R.id.btn_camera);
        calendarNewDate = findViewById(R.id.calendar_new_date);
        addNewPago = findViewById(R.id.add_valor_nuevo);
        progressDialog = new ProgressDialog(this);
        pager_diamantes = findViewById(R.id.pager_diamantes);
        pager_diamantes.setClipToPadding(false);
        pager_diamantes.setClipChildren(false);
        pager_diamantes.setOffscreenPageLimit(3);
        pager_diamantes.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float radio = 1 - Math.abs(position);
                page.setScaleY(0.85f + radio * 0.15f);
            }
        });

        pager_diamantes.setPageTransformer(compositePageTransformer);


        diamantesAdapter = new DiamantesAdapter(this, this, diamanteList, pager_diamantes);
        pager_diamantes.setAdapter(diamantesAdapter);

//        pager_diamantes.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                sliderHandler.removeCallbacks(sliderRuneable);
//                sliderHandler.postDelayed(sliderRuneable,2000);
//            }
//        });

        vender = findViewById(R.id.btn_venta);
        descripcion_opcional = findViewById(R.id.comentario_opcional);
        diamante_text = findViewById(R.id.diamante_texto);
        valor_diamante = findViewById(R.id.valor_diamante);
        fecha_tv = findViewById(R.id.fecha);
        fecha_tv.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date()));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            ventaFecha = date.getTime();
//            System.out.println("venta " + ventaFecha);
////            System.out.println(ventaFecha);
            String output = dateFormat.format(ventaFecha);
            ventaFecha = Long.parseLong(output.replace("-", ""));
//            System.out.println(output); // 2013-12-04
//            fecha_tv.setText(output);
        } catch (Exception e) {
            e.printStackTrace();
        }

        name_user_tv = findViewById(R.id.nombre_user);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        ventaRef = FirebaseDatabase.getInstance().getReference().child("Venta");
        referenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");


        auth = FirebaseAuth.getInstance();
        vendedorUID = auth.getUid();

        getDataUser();

        vender = findViewById(R.id.btn_venta);



        fuser = FirebaseAuth.getInstance().getCurrentUser();

        getUser();

        addNewPago.setOnClickListener(v -> setNewValor());

        calendarNewDate.setOnClickListener(v -> getNewDate());

        btn_addFactura.setOnClickListener(v -> ShowDialogSelected());


        Intent intent = getIntent();
        if (intent!=null && intent.getStringExtra("pid")!= null){
            isUpdate = true;
            ventId = intent.getStringExtra("pid");
            vender.setText("Actualizar");
            ventaRef.child(ventId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("descripcion").exists()){
                        fecha_tv.setText(snapshot.child("fechaVenta").getValue().toString());
                        diamante_texto = snapshot.child("descripcionDiamantes").getValue().toString();
                        diamante_text.setText(diamante_texto);
                        valor_diama = Double.parseDouble(snapshot.child("precioDiamante").getValue().toString());
                        valor_diamante.setText("$"+valor_diama);
                        diamantesAdapter.setValor(valor_diama);
                        diamantesAdapter.notifyDataSetChanged();
                        descripcion = snapshot.child("descripcion").getValue().toString();
                        descripcion_opcional.setText(descripcion);
                        colorVenta = snapshot.child("colorVendedor").getValue().toString();

                        if (snapshot.child("image").getChildrenCount()>0){
                            cuentaFotosSubidas =(int)snapshot.child("image").getChildrenCount();
                            System.out.println("Fotos actuales " + cuentaFotosSubidas);
                            for (DataSnapshot dataSnapshot : snapshot.child("image").getChildren()){
                                fotosList.add(Uri.parse(dataSnapshot.getValue().toString()));
                                listaNueva.add(dataSnapshot.getValue().toString());
                                mediaIdList.add(dataSnapshot.getKey().toString());
                                System.out.println(dataSnapshot.getKey().toString());
                                fotosFacturaAdapters.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        vender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUpdate)
                    saveVenta();
                else {
                    ShowProgress();
                    UpdateVenta();
                }
            }
        });
    }

    private void UpdateVenta(){

        if (textoProgress!=null)textoProgress.setText("Procesando Actualizacion espera....");
        descripcion = descripcion_opcional.getText().toString();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("fechaVenta", fecha_tv.getText().toString());
        hashMap.put("colorVendedor", colorVenta);
        hashMap.put("descripcionDiamantes", diamante_texto);
        hashMap.put("precioDiamante", valor_diama);
        hashMap.put("descripcion", descripcion);


        if (fotosList.size()>cuentaFotosSubidas){
            for (int i = cuentaFotosSubidas; i < fotosList.size(); i++){
                String mediaId = ventaRef.child("image").push().getKey();
                mediaIdList.add(mediaId);
                String addChild = UUID.randomUUID().toString();
                filePath = firebaseStorage.child(addChild + "." + "png");
                File tumb_filePath = new File(fotosList.get(i).getPath());


                try {
                    bitmap = new Compressor(this)
                            .setMaxWidth(400)
                            .setMaxHeight(400)
                            .setQuality(90)
                            .compressToBitmap(tumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();
                if (textoProgress != null) textoProgress.setText("Imagenes por subir " + i);
                firebaseStorage.child(addChild + "." + "png").putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            firebaseStorage.child(addChild + "." + "png").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    System.out.println(task.getResult().toString());
                                    listaNueva.add(task.getResult().toString().toString());



                                    totalMEdia++;
                                    if (textoProgress !=null) textoProgress.setText("Imagenes subidas " + totalMEdia );

                                    if (listaNueva.size() == fotosList.size())
                                        updateVenta(hashMap);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mostrarDialogoError("Error al publicar la venta",e.getMessage());
                        //progressDialog.dismiss();
                        dialogProgress.cancel();
                    }
                });
            }


        }else {
            ventaRef.child(ventId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialogProgress.cancel();
                    finish();
                }
            });
        }



    }

    private void updateVenta(HashMap<String, Object> hashMap) {
        for (int  i= 0; i < listaNueva.size(); i++ ){
            hashMap.put("/image/" + mediaIdList.get(i) + "/",  listaNueva.get(i));
        }
        mediaIdList.clear();
        fotosList.clear();
        totalMEdia = 0;

        ventaRef.child(ventId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialogProgress.cancel();
                finish();
            }
        });
    }


    private Uri photoURI;

    private void ShowDialogSelected(){
        dialogoFoto.setContentView(R.layout.crearfotodialog);
        ImageView camera = dialogoFoto.findViewById(R.id.btn_camera_dialog);
        ImageView galery = dialogoFoto.findViewById(R.id.btn_galery_dialog);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                // Ensure that there's a camera activity to handle the intent
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    // Create the File where the photo should go
//                    File photoFile = null;
//                    try {
//                        photoFile = createImageFile();
//                    } catch (IOException ex) {
//                        // Error occurred while creating the File
//                    }
//                    // Continue only if the File was successfully created
//                    if (photoFile != null) {
//
//
//                        ContentValues values = new ContentValues();
//
//                        values.put(MediaStore.Images.Media.TITLE, "MyPicture");
//                        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
//                        photoURI = getContentResolver().insert(
//                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//                        //Uri photoURI = FileProvider.getUriForFile(AddActivity.this, "com.example.android.fileprovider", photoFile);
//
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                        startActivityForResult(takePictureIntent, 10);
//                    }
//                }

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                         photoURI = FileProvider.getUriForFile(CrearVentaActivity.this,
                                "com.damon.ventadiamante",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 10);
                    }
                }


                dialogoFoto.dismiss();

            }
        });

        galery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,11);
                dialogoFoto.dismiss();
            }
        });

        dialogoFoto.show();
    }
    private static File file;
    String _imageFileName;
    String currentPhotoPath;
    private static final String IMAGE_CAPTURE_FOLDER = "cmscanner";
    private File createImageFile() throws IOException {
        // Create an image file name
        //    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //   String imageFileName = "JPEG_" + timeStamp + "_";
         _imageFileName = UUID.randomUUID().toString();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(storageDir, IMAGE_CAPTURE_FOLDER);
        File image = File.createTempFile(
                //   imageFileName,  /* prefix */
                _imageFileName,
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void getNewDate(){
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
//                inputSearch.setText(dateFormatter.format(newDate.getTime()));
                fecha_tv.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a")
                        .format(newDate.getTime()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                ventaFecha = newDate.getTime().getTime();
                String output = dateFormat.format(ventaFecha);
                ventaFecha = Long.parseLong(output.replace("-",""));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

       StartTime.show();
    }


    private void ShowProgress(){
        dialogProgress.setContentView(R.layout.dialogloading);
        dialogProgress.setCanceledOnTouchOutside(false);
        dialogProgress.setCancelable(false);

        textoProgress = dialogProgress.findViewById(R.id.texto_progress);

        dialogProgress.show();
    }

    private void setDiamanteList(){
        diamanteList.add(new Diamante("100 + BONUS 10 DIAMANTES",1.30,R.drawable.diamantes_free,"#333333"));
        diamanteList.add(new Diamante("200 + BONUS 20 DIAMANTES",2.60,R.drawable.diamantes_free,"#333333"));
        diamanteList.add(new Diamante("310 + BONUS 31 DIAMANTES",3.50,R.drawable.diamantes_free,"#333333"));
        diamanteList.add(new Diamante("520 + BONUS 52 DIAMANTES",5.25,R.drawable.diamantes_free,"#333333"));
        diamanteList.add(new Diamante("620 + BONUS 62 DIAMANTES",6.75,R.drawable.diamantes_free,"#333333"));
        diamanteList.add(new Diamante("825 + BONUS 88 DIAMANTES",8.85,R.drawable.diamantes_free,"#FDBE3B"));
        diamanteList.add(new Diamante("1060 + BONUS 106 DIAMANTES",11.25,R.drawable.diamantes_free,"#FDBE3B"));
        diamanteList.add(new Diamante("1360 + BONUS 147 DIAMANTES",14.75,R.drawable.diamantes_free,"#FDBE3B"));
        diamanteList.add(new Diamante("1540 + BONUS 198 DIAMANTES",16.50,R.drawable.diamantes_free,"#FDBE3B"));
        diamanteList.add(new Diamante("2180 + BONUS 218 DIAMANTES",21.25,R.drawable.diamantes_free,"#FDBE3B"));
        diamanteList.add(new Diamante("2620 + BONUS 350 DIAMANTES",26.00,R.drawable.diamantes_free,"#FDBE3B"));
        diamanteList.add(new Diamante("5600 + BONUS 560 DIAMANTES",51.00,R.drawable.diamantes_free,"#FF4842"));

    }

    private void setNewValor(){
        if (alertDialog == null){
           AlertDialog.Builder builder = new AlertDialog.Builder(CrearVentaActivity.this);
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_respuesta,
                            (ViewGroup)findViewById(R.id.layoutAddUrlContainer));
            builder.setView(view);

            alertDialog = builder.create();
            if (alertDialog.getWindow() !=null){
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputURL = view.findViewById(R.id.inputURL);
            final EditText inputNumber = view.findViewById(R.id.nuber);
            TextView title = view.findViewById(R.id.titulo_dialogo);
            inputNumber.setVisibility(View.VISIBLE);
            title.setText("Escribe el nuevo valor porfavor");
            inputURL.requestFocus();
            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (inputURL.getText().toString().trim().isEmpty() && inputNumber.getText().toString().trim().isEmpty()){
                            Toast.makeText(CrearVentaActivity.this, "Escribe el nuevo valor", Toast.LENGTH_SHORT).show();
                        }else {
                            diamanteList.add(new Diamante(inputURL.getText().toString(),Double.parseDouble(inputNumber.getText().toString()),R.drawable.diamantes_free,"#FF4842"));
                            diamantesAdapter.notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
                    }catch (Exception e){
                        Toast.makeText(CrearVentaActivity.this,"Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
        alertDialog.show();
    }

    @Override
    public void onCLickDiamante(Diamante diamante, int position) {
        System.out.println(diamante.getValor());
        if (diamante!=null){
            diamante_text.setText(diamante.getDiamantes());
            valor_diamante.setText(" $"+diamante.getValor());
            diamante_texto = diamante.getDiamantes();
            valor_diama = diamante.getValor();
            colorVenta = diamante.getColorPrice();
        }
    }

    private void getDataUser(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                reference.child(vendedorUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            nombre_user = snapshot.child("name").getValue().toString();
                            name_user_tv.setText("Vendedor: "+nombre_user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               mostrarDialogoError("Error al recuperar info del usuario /n",error.getMessage());
                            }
                        });
                    }

                });
            }
        }.start();
    }

    int totalMEdia = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    ArrayList<String> listaNueva = new ArrayList<>();

    private void saveVenta(){
        if (TextUtils.isEmpty(diamante_texto)){
            Toast.makeText(this, "Selecciona por favor una tarjeta de valor de venta", Toast.LENGTH_SHORT).show();
            return;
        }
        notify = true;
        progressDialog.setMessage("Espera porfavor...");
        progressDialog.setCanceledOnTouchOutside(false);
        //progressDialog.show();

        ShowProgress();

        descripcion = descripcion_opcional.getText().toString();
        HashMap<String, Object> hashMap = new HashMap<>();

        String ref = ventaRef.push().getKey();

        if (fotosList.size() > 0) {
        for(int i = 0; i <fotosList.size(); i++) {
            final int finalI = i;

            System.out.println("uris " + fotosList.get(i));
            String mediaId = ventaRef.child("image").push().getKey();

            mediaIdList.add(mediaId);



                String addChild = UUID.randomUUID().toString();
                filePath = firebaseStorage.child(addChild + "." + "png");

            System.out.println("filepath" + filePath);
//        final File file = new File(SiliCompressor.with(CrearVentaActivity.this)
//        .compress(FileUtils.getPath(CrearVentaActivity.this,imgURI),
//                new File(CrearVentaActivity.this.getCacheDir(),"temp")));
//
//        Uri uri = Uri.fromFile(file);

                File tumb_filePath = new File(fotosList.get(i).getPath());


                try {
                    bitmap = new Compressor(this)
                            .setMaxWidth(400)
                            .setMaxHeight(400)
                            .setQuality(90)
                            .compressToBitmap(tumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                UploadTask uploadTask = filePath.putBytes(thumb_byte);

//            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//
//                    if (!task.isSuccessful())
//                    throw task.getException();
//
//                    return filePath.getDownloadUrl();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//
//                }
//            });


            if (textoProgress != null) textoProgress.setText("Imagenes por subir " + fotosList.size());

            firebaseStorage.child(addChild + "." + "png").putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        firebaseStorage.child(addChild + "." + "png").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                System.out.println(task.getResult().toString());
                                listaNueva.add(task.getResult().toString().toString());



                                totalMEdia++;
                                if (textoProgress !=null) textoProgress.setText("Imagenes subidas " + totalMEdia );

                                if (listaNueva.size() == fotosList.size())
                                    updateVentaDataBase(ref,hashMap);
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                       mostrarDialogoError("Error al publicar la venta",e.getMessage());
                        //progressDialog.dismiss();
                        dialogProgress.cancel();
                }
            });







//                filePath.putBytes(thumb_byte).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                        if (!task.isSuccessful()) {
//                            throw task.getException();
//                        }
//                        return filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//
//                        if (task.isSuccessful()) {
//                            Uri path = task.getResult();
//
//                            hashMap.put("/image/" + mediaIdList.get(totalMEdia) + "/",  path.toString());
//
//                            totalMEdia++;
//                            if (totalMEdia == fotosList.size())
//                                updateVentaDataBase(ref,hashMap);
//
//                        }
//                    }
//                });
            }

        }else {
            hashMap.put("image", "https://firebasestorage.googleapis.com/v0/b/ventadiamantes-329aa.appspot.com/o/Facturas%2Fae11c5a5-10ad-4c31-ad66-c1478032ad80.jpg?alt=media&token=a5dbc974-eec9-4653-a0f2-cf83ec744691");
//                hashMap.put("vendedorName", nombre_user);
//                hashMap.put("vendedorUID", vendedorUID);
//                hashMap.put("fechaVenta", fecha_tv.getText().toString());
//                hashMap.put("colorValorPorVenta", "");
//                hashMap.put("colorVendedor", colorVenta);
//                hashMap.put("descripcionDiamantes", diamante_texto);
//                hashMap.put("precioDiamante", valor_diama);
//                hashMap.put("descripcion", descripcion);
//                hashMap.put("numeroVenta", ventaFecha);
//                hashMap.put("idVentaRef", ref);

            updateVentaDataBase(ref,hashMap);
        }

    }

    private void pausar(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void updateVentaDataBase(String ref, HashMap<String, Object> hashMap){

        System.out.println("lista nueva "+listaNueva);
        for (int  i= 0; i < listaNueva.size(); i++ ){
            hashMap.put("/image/" + mediaIdList.get(i) + "/",  listaNueva.get(i));
        }

        mediaIdList.clear();
        fotosList.clear();
        totalMEdia = 0;
        hashMap.put("vendedorName", nombre_user);
        hashMap.put("vendedorUID", vendedorUID);
        hashMap.put("fechaVenta", fecha_tv.getText().toString());
        hashMap.put("colorValorPorVenta", "");
        hashMap.put("colorVendedor", colorVenta);
        hashMap.put("descripcionDiamantes", diamante_texto);
        hashMap.put("precioDiamante", valor_diama);
        hashMap.put("descripcion", descripcion);
        hashMap.put("numeroVenta", ventaFecha);
        hashMap.put("idVentaRef", ref);

        ventaRef.child(ref).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                   // progressDialog.dismiss();
                    dialogProgress.cancel();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                    final String msg = "Vendio $" + valor_diama;

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Users contacts = dataSnapshot.getValue(Users.class);
                            if (notify) {
                                sendNotifiaction(messageReciverID, contacts.getName(), msg);
                            }
                            notify = false;
                            sentMessageIntent(valor_diama);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mostrarDialogoError("Error al subir a la base de datos",e.getMessage());
              //  progressDialog.dismiss();
                dialogProgress.cancel();
            }
        });

    }

    private void mostrarDialogoError(String title,String message){
        dialogErrores.setTitle(title);
        dialogErrores.setMessage(message);
        dialogErrores.create();
        dialogErrores.show();
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
                            Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "Nuevo Venta",
                                    messageReciverID);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success != 1) {
                                                    Toast.makeText(CrearVentaActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    private void getUser(){
        referenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                   for (DataSnapshot snapshot1: snapshot.getChildren()){
                       Users users = snapshot1.getValue(Users.class);
                       if (!users.getUid().equals(vendedorUID)){
                           messageReciverID = users.getUid();
                           System.out.println("user"+ messageReciverID);
                       }
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sentMessageIntent(double valor){
        String msg = "Ahoritita hice una recarga de $" + valor;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,msg);
        startActivity(Intent.createChooser(intent,"Enviar con"));
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data !=null && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgURI = result.getUri();

            try {
                ScannerConstants.selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgURI);
                startActivityForResult(new Intent(CrearVentaActivity.this,ImageCropActivity.class),1234);
            } catch (IOException e) {
                e.printStackTrace();
            }




        }else if (requestCode == 10 && resultCode == RESULT_OK ){

                System.out.println(photoURI);
                try {
                    ScannerConstants.selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photoURI);
                    startActivityForResult(new Intent(CrearVentaActivity.this,ImageCropActivity.class),1234);
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }else if (requestCode ==11 && resultCode == RESULT_OK){
            if (data != null){
                photoURI = data.getData();
                try {
                    ScannerConstants.selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photoURI);
                    startActivityForResult(new Intent(CrearVentaActivity.this,ImageCropActivity.class),1234);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        else if (requestCode == 1234 && resultCode == RESULT_OK){
            if (ScannerConstants.selectedImageBitmap != null){

                try {
                    File file = guardarFoto(ScannerConstants.selectedImageBitmap);
                    System.out.println(file.getAbsolutePath());
                    fotosList.add(Uri.fromFile(file));
                    fotosFacturaAdapters.notifyDataSetChanged();
                    System.out.println(fotosList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File guardarFoto(Bitmap data) throws IOException {


        String nombre = UUID.randomUUID().toString();
            File folder;
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
                folder = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+File.separator+getString(R.string.app_name));
            }else {
                folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+getString(R.string.app_name));
            }

            if (!folder.exists()){
                folder.mkdirs();
            }
        File saveImage = new File(folder+File.separator,nombre+".jpg");

            OutputStream outputStream = new FileOutputStream(saveImage);
            data.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();

            return saveImage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000){
            if (grantResults.length > 0){

            }else {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                    ActivityCompat.requestPermissions(CrearVentaActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                }
            }
        }


    }

    private Runnable sliderRuneable = new Runnable() {
        @Override
        public void run() {
            if (pager_diamantes.getCurrentItem() < diamanteList.size()-1){
                pager_diamantes.setCurrentItem(pager_diamantes.getCurrentItem()+1);
            }else {
                pager_diamantes.setCurrentItem(0);
            }
        }
    };

//    @Override
//    protected void onResume() {
//        super.onResume();
//        sliderHandler.postDelayed(sliderRuneable,2000);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        sliderHandler.removeCallbacks(sliderRuneable);
//    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

         File file = new File("/storage/emulated/0/Android/data/com.damon.ventadiamante/files/Pictures");
         deleteDir(file);
        File folder;
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
            folder = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+File.separator+getString(R.string.app_name));
        }else {
            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+getString(R.string.app_name));
        }
        deleteDir(folder);
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