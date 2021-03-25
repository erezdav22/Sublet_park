package com.example.subletpark;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap MapAPI;
    SupportMapFragment mapFragment;

    public Button sendData;
    public Button extrectData;

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private static final String TAG = "MainActivity";
     private static final String KEY_name = "firstname";
    private static final String KEY_lastname = "lastname";

    private EditText editTextfirstname;
    private EditText editTextlastname;
    private TextView TextViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendData=(Button)findViewById(R.id.addData);
        extrectData=(Button)findViewById(R.id.viewData);
        editTextfirstname=findViewById(R.id.editTextfirstname);
        editTextlastname=findViewById(R.id.editTextlastname);
        TextViewData=findViewById(R.id.Text_View_Data);
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);

    }

    public void sendData(View view) {
        String firstname=editTextfirstname.getText().toString();
        String lastname=editTextlastname.getText().toString();

        Map<String, Object> User = new HashMap<>();
        User.put(KEY_name, firstname);
        User.put(KEY_lastname, lastname);
        db.collection("User").add(User)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"error",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

    }


    public void extrectData(View view) {

        db.collection("User").document("test1").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String firstname=documentSnapshot.getString(KEY_name);
                            String lastname=documentSnapshot.getString(KEY_lastname);

                            //Map<String, Object> User=documentSnapshot.getData();

                            TextViewData.setText("First Name: "+firstname+ "\n"+ "Last Name: "+lastname);

                        }else{
                            Toast.makeText(MainActivity.this,"document does not exist",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());

                    }
                });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapAPI=googleMap;

        LatLng Haifa = new LatLng(32.793412716424065, 34.98948248880304);
        MapAPI.addMarker(new MarkerOptions().position(Haifa).title("Haifa"));
        MapAPI.moveCamera(CameraUpdateFactory.newLatLng(Haifa));

    }


    public void signin(View view) {
        startActivity(new Intent(this, login.class));

    }
}