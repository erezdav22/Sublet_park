package com.example.subletpark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class About_the_app extends AppCompatActivity {

    public TextView header;
    public TextView step1;
    public TextView addParkingLink;
    public TextView step2;
    public TextView mainPageLink;
    public TextView step3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_the_app);

        header =  findViewById(R.id.header);
        step1 =  findViewById(R.id.step1);
        addParkingLink =  findViewById(R.id.addParkingLink);
        step2 =  findViewById(R.id.step2);
        mainPageLink =  findViewById(R.id.mainPageLink);
        step3 =  findViewById(R.id.step3);


    }

    public void goToAddParking(View view) {
        startActivity(new Intent(About_the_app.this,addParking.class));

    }

    public void goToMainPage(View view) {
        startActivity(new Intent(About_the_app.this,MainPage.class));

    }
}