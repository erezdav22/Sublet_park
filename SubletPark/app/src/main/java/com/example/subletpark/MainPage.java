
package com.example.subletpark;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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


    private TextView greeting;
    private String name_of_user;

    SeekBar seekBar1;
    TextView radiusHead;
    SeekBar seekBar2;
    TextView priceHead;

    private ParkingAdapter2 adapter2;
    private ParkingAdapter3 adapter3;
    private CollectionReference notebookRef=db.collection("ParkingSpot");
    ArrayList<Parking_Class> datalist;
    String string_start;
    String string_end;
    private Uri imageUri1;
    TextView noParking1;
    final int[] distanceProgress = {1000};
    final int[] priceProgress = {2147483647};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        drawerLayout=findViewById(R.id.root_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        radiusHead=(TextView)findViewById(R.id.radiusHead);
        seekBar1=findViewById(R.id.seekBar1);
        priceHead=(TextView)findViewById(R.id.priceHead);
        seekBar2=findViewById(R.id.seekBar2);


        greeting=findViewById(R.id.greeting);

        greeting=findViewById(R.id.greeting);
        noParking1=findViewById(R.id.noParking1);

        String displayed_greet= null;
        Calendar c=Calendar.getInstance();
        int time_of_day=c.get(Calendar.HOUR_OF_DAY);
        System.out.println("time is: ----->"+time_of_day);
        if (time_of_day>=0 && time_of_day<12)
            displayed_greet= "בוקר טוב";
        else if (time_of_day>=12 && time_of_day<16)
            displayed_greet="צהריים טובים";
        else if (time_of_day>=16 && time_of_day<21)
            displayed_greet="ערב טוב";
        else if (time_of_day>=21 && time_of_day<24)
            displayed_greet="לילה טוב";

        String finalDisplayed_greet = displayed_greet;
        db.collection("User")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            // System.out.println(querySnapshot.getDocuments().get(0).get("lastLoginDate"));
                            name_of_user = querySnapshot.getDocuments().get(0).get("firstname").toString();
                            System.out.println("name in onSuccess is: "+name_of_user);
                            greeting.setText(finalDisplayed_greet+" "+name_of_user+"!");

                        }}});

       /** locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, (LocationListener) this);
**/
      //  mikum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);



        editTextSearch=findViewById(R.id.editTextSearch);
        imageViewSearch=findViewById(R.id.imageViewSearch);
        imageViewSearch.setOnClickListener(this::search);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI2);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapAPI2 = googleMap;

                LatLng latLng = new LatLng(32.069294, 34.774589);

                   // MapAPI2.addMarker(new MarkerOptions().position(latLng));
                    MapAPI2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));


            }
        });




        Places.initialize(getApplicationContext(),"AIzaSyDC8wMP9MaCDDnTmdWeXx1-npixfiQiUug", Locale.forLanguageTag("iw"));

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


        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceProgress[0] =progress;
                radiusHead.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                priceProgress[0] =progress;
                priceHead.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

/**
        db.collection("ParkingSpot").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

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
 numid++;

                        table.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));


                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });**/

        RecyclerView recyclerView = findViewById(R.id.recycler_view_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        datalist=new ArrayList<Parking_Class>();
        adapter3=new ParkingAdapter3(datalist);
        /**
        db.collection("ParkingSpot").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot>list=queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot d:list){

                    String address=d.getData().get("address").toString();
                    String price=d.getData().get("daily price").toString();
                    String desc=d.getData().get("description").toString();
                    String start_date=d.getData().get("start_date").toString();
                    String end_date=d.getData().get("end_date").toString();
                    String uri=d.getData().get("uri").toString();
                    try{
                        string_start=longToDate(start_date);
                        string_end=longToDate(end_date);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                  // imageUri1=Uri.parse(uri);
                  //  Picasso.with(getApplicationContext().lo)
                  Parking_Class parking_class=new Parking_Class(address,price,string_start,string_end,uri,desc);

                    //ParkTest parkTest= new ParkTest(address,price,desc);
                  //  Log.d(TAG, "onSuccess: "+ parkTest.getAddress()+parkTest.getPrice()+parkTest.getDesc());
                    datalist.add(parking_class);
                }
                adapter3.notifyDataSetChanged();
                recyclerView.setAdapter(adapter3);
            }
        });**/


        // setUpRecyclerView();

    }


    /**
    private void setUpRecyclerView() {
       Query query=notebookRef;
        //.orderBy("address",Query.Direction.DESCENDING);
        // Task<QuerySnapshot> query=notebookRef.get();
        System.out.println("--->"+query);


        FirestoreRecyclerOptions<ParkTest> options=new FirestoreRecyclerOptions.Builder<ParkTest>()
                .setQuery(query,ParkTest.class).build();

                adapter2=new ParkingAdapter2(options);
                RecyclerView recyclerView = findViewById(R.id.recycler_view_id);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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

**/

    public String longToDate(String date1)throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        long milliSeconds=Long.parseLong(date1);
        Date date = new Date(milliSeconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        System.out.println(formatter.format(calendar.getTime()));
        System.out.println(formatter.format(date));

        return formatter.format(date);
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

                break;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                break;

            case R.id.nav_addPark:
                startActivity(new Intent(getApplicationContext(), addParking.class));

                break;

            case R.id.nav_MyPark:
                startActivity(new Intent(getApplicationContext(), edit_park.class));

                break;

            case R.id.nav_logout:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), login.class));


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

            CircleOptions circly = new CircleOptions().center(latLng).radius(distanceProgress[0]);
            Circle circle = MapAPI2.addCircle(circly);
            circle.setStrokeColor(Color.RED);
            MapAPI2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));
            // MapAPI2.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 12.0f));



            RecyclerView recyclerView = findViewById(R.id.recycler_view_id);

            db.collection("ParkingSpot").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("RtlHardcoded")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LatLng park_location = new LatLng(Double.parseDouble(document.getData().get("lat").toString()), Double.parseDouble(document.getData().get("lng").toString()));

                            double distance = SphericalUtil.computeDistanceBetween(park_location, latLng);

                            Long currentDate= System.currentTimeMillis();
                            Long endParking= Long.valueOf(document.getData().get("end_date").toString());
                            int currentprice= Integer.parseInt(document.getData().get("daily price").toString());


                            Log.d(TAG, "onComplete: "+distance);

                            if (distance <= distanceProgress[0] && currentDate<endParking && currentprice<=priceProgress[0]) {


                                String uid=document.getData().get("userId").toString();
                                String address=document.getData().get("address").toString();
                                String price=document.getData().get("daily price").toString();
                                String desc=document.getData().get("description").toString();
                                String start_date=document.getData().get("start_date").toString();
                                String end_date=document.getData().get("end_date").toString();
                                String uri=document.getData().get("uri").toString();
                                try{
                                    string_start=longToDate(start_date);
                                    string_end=longToDate(end_date);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                /**  imageUri1=Uri.parse(uri);
                                 Picasso.with(getApplicationContext().lo)**/
                                Parking_Class parking_class=new Parking_Class(uid,address,price,string_start,string_end,uri,desc);

                                //ParkTest parkTest= new ParkTest(address,price,desc);
                                //  Log.d(TAG, "onSuccess: "+ parkTest.getAddress()+parkTest.getPrice()+parkTest.getDesc());
                                datalist.add(parking_class);

                            adapter3.notifyDataSetChanged();





                            }

                        }

                        if(datalist.isEmpty()){
                            noParking1.setVisibility(View.VISIBLE);


                        }

                    } else {
                        Log.d(TAG, "There is no parking available in this place ", task.getException());

                    }
                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height = 450;
                    mapFragment.getView().setLayoutParams(params);
                    recyclerView.setAdapter(adapter3);
                }
            });


        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapAPI2=googleMap;

    }



    public void logout_user(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, login.class));

    }




}
