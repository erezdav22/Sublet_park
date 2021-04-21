package com.example.subletpark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import static com.example.subletpark.R.id.editTextSearch;

public class MainPage extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    GoogleMap MapAPI2;
    SupportMapFragment mapFragment;
    private EditText editTextSearch;
    private ImageView imageViewSearch;
    List<Address> addressList = null;
    Address address=null;
    String location=null;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);


        editTextSearch=findViewById(R.id.editTextSearch);
        imageViewSearch=findViewById(R.id.imageViewSearch);
        imageViewSearch.setOnClickListener(this::search);
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI2);
        mapFragment.getMapAsync(this);




    }

    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_profile:
                startActivity(new Intent(MainPage.this,ProfileActivity.class));

            case R.id.nav_addPark:
                startActivity(new Intent(MainPage.this,addParking.class));

            case R.id.nav_MyPark:
                startActivity(new Intent(MainPage.this,edit_park.class));

            case R.id.nav_logout:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    public void search(View view) {

        String location = editTextSearch.getText().toString();


        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            Toast.makeText(getApplicationContext(),"lat: "+address.getLatitude()+", lng: "+address.getLongitude(),Toast.LENGTH_LONG).show();
            MapAPI2.addMarker(new MarkerOptions().position(latLng).title(location));


        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapAPI2=googleMap;

    }

    public boolean logout_user(MenuItem item){
        switch (item.getItemId()){
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, login.class));
                break;
        }
        return true;
    }



}