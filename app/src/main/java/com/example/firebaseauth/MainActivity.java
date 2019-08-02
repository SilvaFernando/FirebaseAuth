package com.example.firebaseauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.firebaseauth.modelo.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private User user = new User();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conectarBanco();
        sharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        String result = sharedPreferences.getString("LOGIN","");


        if (!result.equals("true")) {
            criarLogin();
        }
    }
    public void criarLogin(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );
        startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(true)
                    .build(),
                    123
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == 123){

            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(response.isNewUser()) {
                this.user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                this.user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                this.user.setValid(false);
            }
            databaseReference.child("User").child(user.getUid()).setValue(user);

            //Condição atendida login feito
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("LOGIN","true");
            editor.apply();
        }else{
            //Ações para problemas com o login

        }
    }
    public void logout(View v){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LOGIN","false");
        editor.apply();
        criarLogin();
    }
    public void conectarBanco(){
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
