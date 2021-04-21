package com.example.subletpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class edit_park extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    Button b;
    ScrollView scrollview;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_park);

        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_MyPark);

        //Dynamic button creation
        scrollview = new ScrollView(this);
        LinearLayout linearlayout = new LinearLayout(this);
        linearlayout.setOrientation(LinearLayout.VERTICAL);
        scrollview.addView(linearlayout);

        for(int i = 0; i<15;i++)
        {
            LinearLayout linear1 = new LinearLayout(this);
            linear1.setOrientation(LinearLayout.HORIZONTAL);
            linearlayout.addView(linear1);
            b = new Button(this);
            b.setText("ערוך פרטי חניה ");
            b.setId(i);
            b.setTextSize(10);
            b.setPadding(8, 3, 8, 3);
            b.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
            b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

            linear1.addView(b);

            CheckBox checkbox = new CheckBox(this);
            checkbox.setId(i);
            checkbox.setText("Wow..");
            linear1.addView(checkbox);

            checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "Checked.."+ arg0.getId(), Toast.LENGTH_SHORT).show();
                }
            });

            b.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "Yipee.."+ v.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        this.setContentView(scrollview);
    }



    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }
    //till here is the dynamic button 
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                startActivity(new Intent(edit_park.this,MainPage.class));

            case R.id.nav_profile:
                startActivity(new Intent(edit_park.this,ProfileActivity.class));

            case R.id.nav_addPark:
                startActivity(new Intent(edit_park.this,addParking.class));

            case R.id.nav_MyPark:
                break;

            case R.id.nav_logout:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}
