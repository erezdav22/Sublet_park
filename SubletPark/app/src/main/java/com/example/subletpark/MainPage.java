package com.example.subletpark;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainPage extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    GoogleMap MapAPI2;
    SupportMapFragment mapFragment;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG ="MainPage";
    LatLng destinationLatLng;


    private TextView greeting;
    private String name_of_user;

    SeekBar seekBar1;
    TextView radiusHead;
    SeekBar seekBar2;
    TextView priceHead;
    TextView avg_price;
    Snackbar snackbar;


    private ParkingAdapter adapter3;
    private CollectionReference notebookRef=db.collection("ParkingSpot");
    ArrayList<Parking_Class> datalist;
    String string_start;
    String string_end;
    TextView noParking1;
    final int[] distanceProgress = {1000};
    final int[] priceProgress = {2147483647};
    private int price_sum = 0;
    private int parking_spots_count = 0;

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
        avg_price=findViewById(R.id.avg_price);


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

                            name_of_user = querySnapshot.getDocuments().get(0).get("firstname").toString();
                            System.out.println("name in onSuccess is: "+name_of_user);
                            greeting.setText(finalDisplayed_greet+" "+name_of_user+"!");

                        }}});





        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI2);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapAPI2 = googleMap;

                LatLng latLng = new LatLng(32.069294, 34.774589);

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


                autocompleteSupportFragment.setText(place.getAddress());
                destinationLatLng = place.getLatLng();
            }

            @Override
            public void onError(@NonNull Status status) {

                Toast.makeText(getApplicationContext(),"אנא הזן כתובת לחיפוש",Toast.LENGTH_LONG).show();
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


        RecyclerView recyclerView = findViewById(R.id.recycler_view_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        datalist=new ArrayList<Parking_Class>();
        adapter3=new ParkingAdapter(datalist);


    }


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
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);

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
                startActivity(new Intent(getApplicationContext(), AddParking.class));

                break;

            case R.id.nav_MyPark:
                startActivity(new Intent(getApplicationContext(), Edit_park.class));

                break;

            case R.id.nav_contract:
                startActivity(new Intent(getApplicationContext(),Contract.class));
                break;

            case R.id.nav_logout:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));


                break;

        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    public void search(View view) {


        price_sum=0;
        parking_spots_count=0;
        MapAPI2.clear();


            try {

        LatLng latLng = new LatLng(destinationLatLng.latitude, destinationLatLng.longitude);

            MapAPI2.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            CircleOptions circly = new CircleOptions().center(latLng).radius(distanceProgress[0]);
            Circle circle = MapAPI2.addCircle(circly);
            circle.setStrokeColor(Color.RED);
            MapAPI2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));



            RecyclerView recyclerView = findViewById(R.id.recycler_view_id);
            datalist.clear();
            adapter3.notifyDataSetChanged();

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
                            int current_price= Integer.parseInt(document.getData().get("daily price").toString());



                            Log.d(TAG, "onComplete: "+distance);

                            if (distance <= distanceProgress[0] && currentDate<endParking && current_price<=priceProgress[0]) {


                                String uid=document.getData().get("userId").toString();
                                String address=document.getData().get("address").toString();
                                String price=document.getData().get("daily price").toString();
                                price_sum = price_sum + Integer.parseInt(price);
                                parking_spots_count = parking_spots_count + 1;
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


                                MapAPI2.addMarker(new MarkerOptions().position(park_location).title(address));

                                Parking_Class parking_class=new Parking_Class(uid,address,price,string_start,string_end,uri,desc);
                                datalist.add(parking_class);

                                adapter3.notifyDataSetChanged();


                            }

                        }

                        if(datalist.isEmpty()){
                            noParking1.setVisibility(View.VISIBLE);
                            avg_price.setVisibility(View.GONE);


                        }else {
                            noParking1.setVisibility(View.GONE);
                            avg_price.setVisibility(View.VISIBLE);
                            avg_price.setText("מחיר חניות ממוצע באזור (ש״ח): " + price_sum / parking_spots_count);

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

            } catch (Exception e) {
                e.printStackTrace();
                snackbar=snackbar.make(view,"משהו השתבש, נסה שנית",Snackbar.LENGTH_INDEFINITE);
                snackbar.setDuration(5000);
                snackbar.setBackgroundTint(Color.rgb(166, 33, 18));
                snackbar.show();

            }


        }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapAPI2=googleMap;

    }



}