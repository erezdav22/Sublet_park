package com.example.subletpark;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.DescriptorProtos;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//import com.firebase.geofire.GeoFireUtils;
//import com.firebase.geofire.GeoLocation;

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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG ="MainPage";
    private int numid=0;
    TableLayout table;

    Location center = new Location("");
    Location test= new Location("");


    float distance_in_meters;
    boolean isWithin1km= distance_in_meters<1000;


   /** double lat = 51.5074;
    double lng = 0.1278;
    String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));

    ArrayList arr = new ArrayList<DocumentSnapshot>();
    db.collection("ParkingSpots").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    GeoLocation g  = document.getData().getString("geoPoint");
                    if(g.equals(center))
                        arr.add(document);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        }
    }); **/




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
        table=findViewById(R.id.table1);


        editTextSearch=findViewById(R.id.editTextSearch);
        imageViewSearch=findViewById(R.id.imageViewSearch);
        imageViewSearch.setOnClickListener(this::search);
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI2);
        mapFragment.getMapAsync(this);

        center.setLatitude(32.811049);
        center.setLongitude(34.980722);
        test.setLatitude(29.557792);
        test.setLongitude(34.960850);
        System.out.println("distance in m: "+center.distanceTo(test));
        float distance_in_meters;


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


        db.collection("ParkingSpot").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                         TableRow row = new TableRow(MainPage.this);
                         TextView serialText=new TextView(MainPage.this);
                         serialText.setText(document.getData().get("city").toString()+'\n');
                         row.addView(serialText);
                         serialText.setId(numid);
                         numid++;


                         TextView streetText=new TextView(MainPage.this);
                         streetText.setText(document.getData().get("street").toString()+' ');
                         row.addView(streetText);
                         streetText.setId(numid);
                         numid++;

                         TextView streetNumText=new TextView(MainPage.this);
                         streetNumText.setText(document.getData().get("street").toString()+' ');
                         row.addView(streetNumText);
                         streetNumText.setId(numid);
                         numid++;

                         table.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));


                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });



    }

    public void parkingFinder(Location user_location) {
        center.setLatitude(user_location.getLatitude());
        center.setLongitude(user_location.getLongitude());
        test.setLatitude(29.557792);
        test.setLongitude(34.960850);
        distance_in_meters = center.distanceTo(test);
        System.out.println("distance in m: " + distance_in_meters);

        if (distance_in_meters < 1000) {
            //present parking spot in page

        }
    }

    public Location addressToLatLng() {

        String location = editTextSearch.getText().toString();
        Location user_location = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            user_location.setLatitude(address.getLatitude());
            user_location.setLongitude(address.getLongitude());

            return user_location;


        }
        return null;
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