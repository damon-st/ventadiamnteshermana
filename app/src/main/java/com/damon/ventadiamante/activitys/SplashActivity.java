package com.damon.ventadiamante.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.conexcion.CheckNetworkConnection;

public class SplashActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new CheckNetworkConnection(SplashActivity.this, new CheckNetworkConnection.OnConnectionCallback() {

            @Override
            public void onConnectionSuccess() {
                System.out.println("Exito ");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },2000);
            }

            @Override
            public void onConnectionFail(String msg) {
                System.out.println("Noooo ");
                Toast.makeText(SplashActivity.this, "onFail()" + msg, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplashActivity.this, ConexionError.class);
                startActivity(intent);
                finish();
            }
        }).execute();
    }
}