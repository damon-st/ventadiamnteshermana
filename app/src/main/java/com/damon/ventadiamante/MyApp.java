package com.damon.ventadiamante;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public  class MyApp  extends Application {


    private static MyApp instance;
    private static Context appContext;

    public static MyApp getInstance(){return instance;}

    public static Context getAppContext(){return  appContext;}

    public void setAppContext(Context mAppContext){
        appContext = mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.setAppContext(getApplicationContext());
       // AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        FirebaseApp.initializeApp(this.getApplicationContext());
          FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public MyApp(){

    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
//        FirebaseApp.initializeApp(this.getApplicationContext());
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
