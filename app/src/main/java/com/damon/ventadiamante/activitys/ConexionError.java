package com.damon.ventadiamante.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.damon.ventadiamante.R;

public class ConexionError extends AppCompatActivity {

    NetworkInfo networkInfo;

    private int numIntentos = 3;
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
        numIntentos--;
        if (numIntentos ==0){
            startActivity(new Intent(ConexionError.this,LoginActivity.class));
            finish();

        }else {
            if (networkInfo !=null && networkInfo.isConnected()){
                startActivity(new Intent(ConexionError.this,LoginActivity.class));
                finish();
            }
        }

        Toast.makeText(this, "Numero de Intentos antes cargar datos de cache N°" + numIntentos, Toast.LENGTH_SHORT).show();
    }

}