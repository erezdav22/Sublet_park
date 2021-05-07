package com.example.subletpark;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import com.firebase.geofire.GeoFireUtils;
//import com.firebase.geofire.GeoLocation;

public class MainPage extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, LocationListener {

    GoogleMap MapAPI2;
    SupportMapFragment mapFragment;
    private EditText editTextSearch;
    private ImageView imageViewSearch;
    List<Address> addressList = null;
    Address address = null;
    String location = null;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MainPage";
    private int numid = 0;
    TableLayout table;
    TableLayout resultTable;
    Button button;

    LocationManager locationManager;
    Location mikum;
   // List<Parking_Class>parkings = null;
    List<Parking_Class> parkings=new ArrayList<Parking_Class>();

    private ParkingAdapter2 adapter2;
    private CollectionReference notebookRef=db.collection("ParkingSpot");


    /** double lat = 51.5074;
     double lng = 0.1278;
     String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));

     ArrayList arr = new ArrayList<DocumentSnapshot>();
     db.collection("ParkingSpots").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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

        drawerLayout = findViewById(R.id.root_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        table = findViewById(R.id.table1);
        resultTable = findViewById(R.id.resultTable);
        button=findViewById(R.id.button);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

        mikum=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        editTextSearch=findViewById(R.id.editTextSearch);
        imageViewSearch=findViewById(R.id.imageViewSearch);
        imageViewSearch.setOnClickListener(this::search);
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI2);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapAPI2 = googleMap;

                LatLng latLng = new LatLng(mikum.getLatitude(), mikum.getLongitude());
                if (latLng != null || !latLng.equals("")) {

                    MapAPI2.addMarker(new MarkerOptions().position(latLng));
                    MapAPI2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                }

            }
        });



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



                        String uid=document.getData().get("userId").toString();

                        String address=document.getData().get("address").toString();
                        String price=document.getData().get("daily price").toString();
                        String start_date=document.getData().get("start_date").toString();
                        String end_date=document.getData().get("end_date").toString();
                        String desc=document.getData().get("description").toString();
                        String uri=document.getData().get("uri").toString();


                        parkings.add(new Parking_Class(uid,address,price,start_date,end_date,uri,desc));



                         TableRow row = new TableRow(MainPage.this);
                         TextView serialText=new TextView(MainPage.this);
                         serialText.setText(document.getData().get("address").toString()+'\n');
                         row.addView(serialText);
                         serialText.setId(numid);
                         numid++;

                         /**  TextView streetText=new TextView(MainPage.this);
                         streetText.setText(document.getData().get("street").toString()+' ');
                         row.addView(streetText);
                         streetText.setId(numid);
                         numid++;

                         TextView streetNumText=new TextView(MainPage.this);
                         streetNumText.setText(document.getData().get("street_number").toString()+' ');
                         row.addView(streetNumText);
                         streetNumText.setId(numid);
                         numid++;**/

                         table.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));


                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


        //CardFragment cardFragment=new CardFragment(parkings);
       // getSupportFragmentManager().beginTransaction().add(cardFragment,null).commit();
       // getSupportFragmentManager().beginTransaction().add(R.id.root_layout,cardFragment,null).commit();

        setUpRecyclerView();


    }

    private void setUpRecyclerView() {
       Query query=notebookRef.orderBy("address",Query.Direction.DESCENDING);
       // Task<QuerySnapshot> query=notebookRef.get();


        FirestoreRecyclerOptions<ParkTest> options=new FirestoreRecyclerOptions.Builder<ParkTest>()
                .setQuery(query,ParkTest.class).build();

        adapter2=new ParkingAdapter2(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter2);

    }
      @Override
    protected void onStart() {

        super.onStart();
        adapter2.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter2.stopListening();
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
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_home:

               // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuMainPage()).commit();
               // getSupportFragmentManager().beginTransaction().add(R.id.root_layout,new MenuEditPark(),null).commit();
                break;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                //getSupportFragmentManager().beginTransaction().add(R.id.root_layout,new MenuEditPark(),null).commit();

            // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuProfile()).commit();
                break;

            case R.id.nav_addPark:
                startActivity(new Intent(getApplicationContext(), addParking.class));
               // getSupportFragmentManager().beginTransaction().add(R.id.root_layout,new MenuEditPark(),null).commit();

            // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuAddPark()).commit();
                break;

            case R.id.nav_MyPark:
                startActivity(new Intent(getApplicationContext(), edit_park.class));
               // getSupportFragmentManager().beginTransaction().add(R.id.root_layout,new MenuEditPark(),null).commit();
                break;

            case R.id.nav_logout:
                //getSupportFragmentManager().beginTransaction().add(R.id.root_layout,new MenuEditPark(),null).commit();
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), login.class));

            // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuLogOut()).commit();
                break;

        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    public void search(View view) {

        String location = editTextSearch.getText().toString();

        numid=0;


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

            CircleOptions circly = new CircleOptions().center(latLng).radius(1000);
            Circle circle = MapAPI2.addCircle(circly);
            circle.setStrokeColor(Color.RED);
           MapAPI2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));
           // MapAPI2.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 12.0f));
            table.setVisibility(View.GONE);




            db.collection("ParkingSpot").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("RtlHardcoded")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LatLng park_location = new LatLng(Double.parseDouble(document.getData().get("lat").toString()), Double.parseDouble(document.getData().get("lng").toString()));

                            double distance = SphericalUtil.computeDistanceBetween(park_location, latLng);

                            Log.d(TAG, "onComplete: "+distance);

                            if (distance < 1000) {
                                //present parking spot in page
                                TableRow row = new TableRow(MainPage.this);
                                TextView serialText=new TextView(MainPage.this);
                                serialText.setText(document.getData().get("address").toString()+'\n');
                                row.addView(serialText);
                                serialText.setId(numid);
                                serialText.setGravity(Gravity.RIGHT | Gravity.TOP);
                                numid++;


                              /**  TextView streetText=new TextView(MainPage.this);
                                streetText.setText(document.getData().get("street").toString()+' ');
                                row.addView(streetText);
                                streetText.setId(numid);
                                streetText.setGravity(Gravity.RIGHT | Gravity.TOP);
                                numid++;

                                TextView streetNumText=new TextView(MainPage.this);
                                streetNumText.setText(document.getData().get("street_number").toString()+' ');
                                row.addView(streetNumText);
                                streetNumText.setId(numid);
                                streetNumText.setGravity(Gravity.RIGHT | Gravity.TOP);
                                numid++;**/

                                resultTable.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                                resultTable.setGravity(Gravity.RIGHT | Gravity.TOP);
                                resultTable.setVisibility(View.VISIBLE);


                            }

                        }
                    } else {
                        Log.d(TAG, "There is no parking available in this place ", task.getException());
                    }
                }
            });


        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapAPI2=googleMap;
        LatLng latLng = new LatLng(mikum.getLatitude(), mikum.getLongitude());
        if (latLng != null || !latLng.equals("")) {

            MapAPI2.addMarker(new MarkerOptions().position(latLng));
            MapAPI2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
        }

    }



    public void logout_user(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, login.class));

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (latLng != null || !latLng.equals("")) {

            MapAPI2.addMarker(new MarkerOptions().position(latLng));
            MapAPI2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
        }


    }
}