package com.example.subletpark;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

       Places.initialize(getApplicationContext(),"AIzaSyDC8wMP9MaCDDnTmdWeXx1-npixfiQiUug");

        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteSupportFragment=(AutocompleteSupportFragment)getSupportFragmentManager()
                .findFragmentById(R.id.autoComplete_fragment);

        autocompleteSupportFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                editTextSearch.setText(place.getAddress());
                autocompleteSupportFragment.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {

                Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_LONG).show();
            }
        });

//        editTextSearch.setFocusable(false);
//
//        editTextSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<Place .Field> fieldList= Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
//                Intent intent=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(MainPage.this);
//                startActivityForResult(intent,200);
//            }
//        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==200&& requestCode==RESULT_OK){
            Place place= Autocomplete.getPlaceFromIntent(data);
            editTextSearch.setText(place.getAddress());

        }else if(requestCode== AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_LONG).show();
        }
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

              //  FirebaseAuth.getInstance().signOut();

                //startActivity(new Intent(MainPage.this, login.class));

                //finish();


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