package com.damon.ventadiamante.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;

import com.damon.ventadiamante.R;

public class ConexionError extends AppCompatActivity {

    NetworkInfo networkInfo;

    private Button reintentar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexion_error);


        reintentar = findViewById(R.id.reintenar);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        reintentar.setOnClickListener(v -> reintentarConexcion());
    }

    private void reintentarConexcion() {

        if (networkInfo !=null && networkInfo.isConnected()){
            startActivity(new Intent(ConexionError.this,LoginActivity.class));
            finish();
        }else {
            recreate();
        }
    }

}