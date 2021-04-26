package com.example.subletpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import lombok.Data;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.security.AccessController.getContext;

public class edit_park extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG ="editParking";
    private int num=0;
    private int numid=0;
    TableLayout table;
    String parkid;
    List<String> group;
    ListView list;
    List<Parking_Class> user_parking=new ArrayList<Parking_Class>();



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
        table=findViewById(R.id.table);
        list=findViewById(R.id.list);

        db.collection("User")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                group = (List<String>) document.get("parking spots");
                for (String parking_spot:group) {
                    Log.d(TAG, "onComplete: "+parking_spot);
                    db.collection("ParkingSpot").whereEqualTo(FieldPath.documentId(),parking_spot).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d(TAG, "onComplete: "+queryDocumentSnapshots.getDocuments().get(0).get("city").toString());



                            String city=queryDocumentSnapshots.getDocuments().get(0).get("city").toString();
                            String street=queryDocumentSnapshots.getDocuments().get(0).get("street").toString();
                            String street_number=queryDocumentSnapshots.getDocuments().get(0).get("street_number").toString();
                            String price=queryDocumentSnapshots.getDocuments().get(0).get("daily price").toString();
                            String start_date=queryDocumentSnapshots.getDocuments().get(0).get("start_date").toString();
                            String end_date=queryDocumentSnapshots.getDocuments().get(0).get("end_date").toString();
                            String desc=queryDocumentSnapshots.getDocuments().get(0).get("description").toString();
                            String uri=queryDocumentSnapshots.getDocuments().get(0).get("uri").toString();

                            //Parking_Class park=new Parking_Class(parking_spot,city,street,street_number,price,start_date,end_date,uri,desc);

//                            user_parking.add(Parking_Class.builder().city(queryDocumentSnapshots.getDocuments().get(0).get("city").toString()).id(parking_spot).
//                                    street(queryDocumentSnapshots.getDocuments().get(0).get("street").toString()).street_num(queryDocumentSnapshots.getDocuments().get(0).get("street_number").toString()).
//                                    price(queryDocumentSnapshots.getDocuments().get(0).get("daily price").toString()).start_date(queryDocumentSnapshots.getDocuments().get(0).get("start_date").toString()).
//                                    end_date(queryDocumentSnapshots.getDocuments().get(0).get("end_date").toString()).desc(queryDocumentSnapshots.getDocuments().get(0).get("description").toString()).
//                                    uri(queryDocumentSnapshots.getDocuments().get(0).get("uri").toString()).build());


                                user_parking.add(new Parking_Class(parking_spot,city,street,street_number,price,start_date,end_date,uri,desc));

//                            TableRow row = new TableRow(edit_park.this);
//                            TextView serialText=new TextView(edit_park.this);
//                            serialText.setText(queryDocumentSnapshots.getDocuments().get(0).get("city").toString()+'\n');
//                            row.addView(serialText);
//                            serialText.setId(numid);
//                            numid++;
//
//
//                            TextView streetText=new TextView(edit_park.this);
//                            streetText.setText(queryDocumentSnapshots.getDocuments().get(0).get("street").toString()+' ');
//                            row.addView(streetText);
//                            streetText.setId(numid);
//                            numid++;
//
//                            TextView streetNumText=new TextView(edit_park.this);
//                            streetNumText.setText(queryDocumentSnapshots.getDocuments().get(0).get("street").toString()+' ');
//                            row.addView(streetNumText);
//                            streetNumText.setId(numid);
//                            numid++;
//
//                            table.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));

                        }
                    });

                }

            }
        });



        ArrayAdapter<Parking_Class> myAdapter= new ArrayAdapter<Parking_Class>(this, android.R.layout.simple_list_item_1,user_parking);

        list.setAdapter(myAdapter);
        list.setOnItemClickListener(listclick);

    }

    private AdapterView.OnItemClickListener listclick= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            String itemValue=(String) list.getItemAtPosition(position);
            startActivity(new Intent(edit_park.this,addParking.class));

        }
    };


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
