package com.damon.ventadiamante.activitys;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.conexcion.CheckNetworkConnection;
import com.damon.ventadiamante.notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoginActivity extends AppCompatActivity {
    //aqui crearemos la conexion con la base de datos para la autentificacion de inicio
    // private FirebaseUser currentUser;
    //creamos un firebaseAuth que se encarga de la autentificacion d inicio
    private FirebaseAuth mAuth;
    //creamos el dialogo que se mostrala el proceso de inicio al usuario
    private ProgressDialog loadignBar;
    private Button PhoneLoginButton,NeddNewYouAccountLink;
    private ImageButton Loginbutton;
    private EditText UserEmail,UserPassword;
    private TextView ForgetPasswordLink,textInfo;


    private DatabaseReference usersRef;
    private static final int RC_VIDEO_APP_PERM = 124;
    NetworkInfo networkInfo;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new Dialog(this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        mAuth = FirebaseAuth.getInstance();//creamos su instancia
        //   currentUser = mAuth.getCurrentUser();//recupera si ya hay iniciado la cesion

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ForgetPasswordLink = findViewById(R.id.forgert_password_link);


        InitializeFields();

//        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Login.this, ResetPasswordActivity.class));
//            }
//        });

        //boton para crear la cuenta
        NeddNewYouAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToRegisterActivity();
            }
        });

        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();

            }
        });

        //  requestPermissions();

    }

    private void AllowUserToLogin() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Porfavor ingresa el correo", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Porfavor ingresa la contraseña",Toast.LENGTH_SHORT).show();

        }
        else{
            //creamos la vista del proceso que se mostrara al usuario
            loadignBar.setTitle("Iniciando....");//este es el titulo a mostral
            loadignBar.setMessage("Porfavor espera....");//el contenido que tendra el cuadro
            loadignBar.setCanceledOnTouchOutside(true);//y aqui es para que no toque la pantalla
            loadignBar.show();//el show es para que se pueda ver en la pantalla

            //aqui comprobamos si es correcto los usuarios o contraseña
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                //este codigo es apra crear las notificaciones
                                String currentUserID =mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                usersRef.child(currentUserID).child("device_token")
                                        .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    SendToMainActivity();//si es correcto ira ala activity principal
                                                    Toast.makeText(LoginActivity.this, "Inicio de Seccion Correcto..", Toast.LENGTH_SHORT).show();
                                                    loadignBar.dismiss();
                                                }

                                            }
                                        });

                            } else {
                                String messenge = task.getException().toString();
                                // Toast.makeText(Login.this, "Error:"+messenge, Toast.LENGTH_SHORT).show();
                                loadignBar.dismiss();
                                System.out.println(messenge);
                                if (messenge.equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.")){
                                    mostrarDialog("Por Favor Introdusca una direccion de correo Electronica Valida");
                                }else if (messenge.equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.")){
                                    mostrarDialog("La contraseña Ingresada es incorrecta por favor Intenta Nueva Mente");
                                }else if (messenge.equals("com.google.firebase.FirebaseTooManyRequestsException: We have blocked all requests from this device due to unusual activity. Try again later. [ Too many unsuccessful login attempts. Please try again later. ]")){
                                    mostrarDialog("Lo sentimos as utilizado demasiados intentos y emos bloqueado las peticiones por favor intentalo mas tarde");
                                    Loginbutton.setEnabled(false);
                                    textInfo.setText("As utilizado demasiados intentos de inicio de seccion porfavor Intenta mas tarde");
                                }else if (messenge.equals("com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.")){
                                    mostrarDialog("El correo ingresado no existe en nuestra base de Datos por favor revisalo e intenta nueva mente");
                                }else {
                                    mostrarDialog(messenge);
                                }
                            }
                        }
                    });

        }


//        new CheckNetworkConnection(LoginActivity.this, new CheckNetworkConnection.OnConnectionCallback() {
//
//            @Override
//            public void onConnectionSuccess() {
//                System.out.println("Exito ");
//                Toast.makeText(LoginActivity.this, "onSuccess()", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onConnectionFail(String msg) {
//                System.out.println("Noooo ");
//                Toast.makeText(LoginActivity.this, "onFail()", Toast.LENGTH_SHORT).show();
//            }
//        }).execute();

    }



    @Override
    protected void onStart() {
        super.onStart();

        new CheckNetworkConnection(LoginActivity.this, new CheckNetworkConnection.OnConnectionCallback() {
//
            @Override
            public void onConnectionSuccess() {
                System.out.println("Exito ");
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser !=null){
                    if (networkInfo != null && networkInfo.isConnected()){
                        updateToken();
                        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(LoginActivity.this, ConexionError.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }

            @Override
            public void onConnectionFail(String msg) {
                System.out.println("Noooo ");
                Toast.makeText(LoginActivity.this, "onFail()" + msg, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, ConexionError.class);
                startActivity(intent);
                finish();
            }
        }).execute();




    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
        }

        return false;

    }


    private boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress!=null && !inetAddress.equals("");
    }


    private boolean isInternetAvailable(){
        try {
            InetAddress inetAddress = InetAddress.getByName("www.google.com");
            return !inetAddress.equals("");
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean isConnected() throws IOException, InterruptedException {
        final String comand = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(comand).waitFor() == 0;
    }

    private void updateToken(){

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                Token token1 = new Token(s);

                reference.child(firebaseUser.getUid()).setValue(token1);
            }
        });


    }
    private void InitializeFields() {

        Loginbutton =findViewById(R.id.signin);
        UserEmail = findViewById(R.id.email);
        UserPassword = findViewById(R.id.password);
        NeddNewYouAccountLink = findViewById(R.id.signup);
        ForgetPasswordLink = findViewById(R.id.checkbox);
        loadignBar = new ProgressDialog(this);
        textInfo = findViewById(R.id.texto_info);

    }

    //metodo para ir al main activiti
    private void SendToMainActivity() {
        Intent MainIntent= new Intent(LoginActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void SendToRegisterActivity(){
        //metodo spara ir a crear la cuenta
        Intent registerIntent= new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void mostrarDialog(String message){
        dialog.setContentView(R.layout.dialogoalerta);
        TextView titulo = dialog.findViewById(R.id.texto_error);
        ImageView imagen = dialog.findViewById(R.id.imagen_error);
        titulo.setText(message);
        dialog.show();
    }
}