package com.example.subletpark;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private EditText firstName2;
    private EditText lastName2;
    private EditText editTextPhone3;
    private EditText editTextEmail2;
    private EditText editTextPassword3;
    private EditText editTextPassword4;
    private Button updateButton;
    private Button ChangeButton;
    Snackbar snackbar;
    private TextView deleteAccount;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    List<String> group;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    private static final String TAG = "ProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        drawerLayout=findViewById(R.id.activity_profile);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);


        firstName2 = findViewById(R.id.firstName2);
        lastName2 = findViewById(R.id.lastName2);
        editTextPhone3 = findViewById(R.id.editTextPhone3);
        editTextEmail2 = findViewById(R.id.editTextEmail2);
        updateButton=findViewById(R.id.updateButton);
         editTextPassword3 = findViewById(R.id.editTextPassword3);
        editTextPassword4 = findViewById(R.id.editTextPassword4);
        ChangeButton=findViewById(R.id.ChangeButton);
        updateButton.setOnClickListener(this::valid);
        deleteAccount=findViewById(R.id.deleteAccount);


        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder= new AlertDialog.Builder(ProfileActivity.this);
                builder.setCancelable(true);
                builder.setTitle("האם אתה בטוח שאתה רוצה למחוק את חשבונך?");
                builder.setMessage("פעולה זו בלתי הפיכה");

                builder.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        delete_profile();

                    }
                });
                builder.show();


            }
        });





        db.collection("User")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {

                                firstName2.setText(querySnapshot.getDocuments().get(0).get("firstname").toString());
                                lastName2.setText(querySnapshot.getDocuments().get(0).get("lastname").toString());
                                editTextPhone3.setText(querySnapshot.getDocuments().get(0).get("phone").toString());
                                editTextEmail2.setText(querySnapshot.getDocuments().get(0).get("email").toString());

                        } else {

                            View view= findViewById(R.id.activity_profile);
                            snackbar=snackbar.make(view,"נסה שנית",Snackbar.LENGTH_INDEFINITE);
                            snackbar.setDuration(5000);
                            snackbar.setBackgroundTint(Color.rgb(166, 33, 18));
                            snackbar.show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        View view= findViewById(R.id.activity_profile);
                        snackbar=snackbar.make(view,"נסה שנית",Snackbar.LENGTH_INDEFINITE);
                        snackbar.setDuration(5000);
                        snackbar.setBackgroundTint(Color.rgb(166, 33, 18));
                        snackbar.show();
                        Log.d(TAG, e.toString());

                    }
                });
    }

    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            startActivity(new Intent(ProfileActivity.this, MainPage.class));

        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), MainPage.class));
                break;
            case R.id.nav_profile:


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

    public void valid(View view) {

        String phone=editTextPhone3.getText().toString();
        String first_name=firstName2.getText().toString();
        String last_name=lastName2.getText().toString();
        String email=editTextEmail2.getText().toString();


        if(first_name.isEmpty()){

            firstName2.setError("נא להזין שם פרטי");
            firstName2.requestFocus();
            return;

        }



        if(!Pattern.matches("[a-zA-Z\\u0590-\\u05FF]+",first_name)){

            firstName2.setError("נא להזין שם המכיל אותיות בלבד");
            firstName2.requestFocus();
            return;

        }



        if(last_name.isEmpty()){

            lastName2.setError("נא להזין שם משפחה");
            lastName2.requestFocus();
            return;

        }

        if(!Pattern.matches("[a-zA-Z\\u0590-\\u05FF]+",last_name)){

            lastName2.setError("נא להזין שם המכיל אותיות בלבד");
            lastName2.requestFocus();
            return;

        }


        if(phone.isEmpty()){

            editTextPhone3.setError("בבקשה הקלד מספר טלפון");
            editTextPhone3.requestFocus();
            return;

        }


        if(email.isEmpty()){

            editTextEmail2.setError("הקלד מייל בבקשה");
            editTextEmail2.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail2.setError("בבקשה הקלד מייל תקין");
            editTextEmail2.requestFocus();
            return;
        }




        update();

    }

    public void update(){


            db.collection("User")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("phone",editTextPhone3.getText().toString(),
                    "email",editTextEmail2.getText().toString(),"firstname",firstName2.getText().toString(),
                    "lastname",lastName2.getText().toString()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                View view= findViewById(R.id.activity_profile);
                snackbar=snackbar.make(view,"לא התבצע שינוי, נסה שנית",Snackbar.LENGTH_INDEFINITE);
                snackbar.setDuration(5000);
                snackbar.setBackgroundTint(Color.rgb(166, 33, 18));
                snackbar.show();
                Log.d(TAG, e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    View view= findViewById(R.id.activity_profile);
                    snackbar=snackbar.make(view,"פרטי הפרופיל עודכנו בהצלחה!",Snackbar.LENGTH_INDEFINITE);
                    snackbar.setDuration(5000);
                    snackbar.setBackgroundTint(Color.rgb(13, 130, 101));
                    snackbar.show();


                }
            });


    }

    public void change_password(View view) {

        editTextPassword3.setVisibility(View.VISIBLE);
        editTextPassword4.setVisibility(View.VISIBLE);
        ChangeButton.setVisibility(View.VISIBLE);

    }


    public void change(View view) {

        String password=editTextPassword3.getText().toString();
        String password2=editTextPassword4.getText().toString();

        if(password.isEmpty()){

            editTextPassword3.setError("הכנס את סיסמתך החדשה");
            editTextPassword3.requestFocus();
            return;

        }


        if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",password)){

            editTextPassword3.setError("הסיסמה צריכה להכיל מינימום של 8 תווים, מתוכם לפחות אות אחת ומספר אחד!");
            editTextPassword3.requestFocus();
            return;

        }

        if(!password.equals(password2)||password2.isEmpty()){
            editTextPassword4.setError("בבקשה הקלד את אותה הסיסמה בשנית");
            editTextPassword4.requestFocus();
            return;
        }

        FirebaseAuth.getInstance().getCurrentUser().updatePassword(editTextPassword3.getText().toString()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                snackbar=snackbar.make(view,"לא התבצע שינוי, נסה שנית",Snackbar.LENGTH_INDEFINITE);
                snackbar.setDuration(5000);
                snackbar.setBackgroundTint(Color.rgb(166, 33, 18));
                snackbar.show();
                Log.d(TAG, e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                snackbar=snackbar.make(view,"סיסמתך עודכנה בהצלחה!",Snackbar.LENGTH_INDEFINITE);
                snackbar.setDuration(5000);
                snackbar.setBackgroundTint(Color.rgb(13, 130, 101));
                snackbar.show();

            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



    public void delete_profile() {


        db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.get("parking spots")!=null) {
                    group = (List<String>) document.get("parking spots");

                    for (String d : group) {
                        String curr = d;
                        db.collection("ParkingSpot").document(d).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Parking successfully deleted!");
                            }


                        });
                    }
                }
                db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Profile successfully deleted!");
                                Toast.makeText(getApplicationContext(),"מצטערים שעזבת אותנו, חשבונך נמחק בהצלחה",Toast.LENGTH_LONG).show();


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                    startActivity(new Intent(ProfileActivity.this, Login.class));
                                }
                            }
                        });


            }
        });



    }



}





