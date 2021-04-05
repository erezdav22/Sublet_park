package com.example.subletpark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import static com.example.subletpark.R.id.editTextSearch;

public class MainPage extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap MapAPI2;
    SupportMapFragment mapFragment;
    private TextView textViewEdit;
    private EditText editTextSearch;
    private ImageView imageViewSearch;
    List<Address> addressList = null;
    Address address=null;
    String location=null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        textViewEdit=findViewById(R.id.textViewEdit);
        editTextSearch=findViewById(R.id.editTextSearch);
        imageViewSearch=findViewById(R.id.imageViewSearch);
        textViewEdit.setOnClickListener(this::edit_profile);
        imageViewSearch.setOnClickListener(this::search);
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI2);
        mapFragment.getMapAsync(this);




    }

    public void edit_profile(View view) {

        startActivity(new Intent(MainPage.this,ProfileActivity.class));
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



}