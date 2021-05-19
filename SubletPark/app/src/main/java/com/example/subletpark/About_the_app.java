package com.example.subletpark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class About_the_app extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_the_app);



    }


    public void move_to_login(View view) {
        startActivity(new Intent(About_the_app.this,Login.class));
    }
}