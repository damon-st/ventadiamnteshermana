package com.damon.ventadiamante.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.damon.ventadiamante.R;

public class FacturaWebView extends AppCompatActivity {


    private WebView web_factura;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_web_view);

        web_factura = findViewById(R.id.web_factura);

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            id = intent.getStringExtra("id");

            web_factura.setWebViewClient(new WebViewClient());

            web_factura.loadUrl("https://ventadiamantes-329aa.firebaseapp.com/factura/"+id);

            WebSettings webSettings = web_factura.getSettings();
            webSettings.setJavaScriptEnabled(true);



        }
    }
}