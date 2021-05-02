
package com.example.subletpark;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//this class will check current session of firebase user
//if user has not signed out- user will be redirected to main page
public class Home extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            Intent intent= new Intent(Home.this,MainPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

        }
    }
}
