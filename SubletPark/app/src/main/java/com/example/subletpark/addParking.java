package com.example.subletpark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class addParking extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, NavigationView.OnNavigationItemSelectedListener {

    TextView editTextDateTime;
    int day,month,year, hour,minute;
    int finalDay,finalMonth,finalYear,finalHour,finalMinute;

    EditText editTextCity;
    EditText editTextStreet;
    EditText editTextStreetNumber;
    EditText editTextDescription;
    EditText editTextDailyPrice;
    ImageView uploadPic;
    Button buttonUpload;
    private Uri imageUri;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    UploadTask uploadTask;
    private TextView editTextEndDate;
    Long long_start;
   
    private static final String KEY_city = "city";
    private static final String KEY_street= "street";
    private static final String KEY_street_number = "street_number";
    private static final String KEY_daily_price= "daily price";
    private static final String description = "description";
    private static final String start_date = "start_date";
    private static final String end_date = "end_date";
    private static final String userId = "userId";
    private static final String URI = "uri";
    private static final String TAG ="addParking";
    private static final int PICK_IMAGE=1;


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking);


        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_addPark);


        editTextDateTime = findViewById(R.id.editTextDateTime);
        editTextDateTime.setOnClickListener(this::picker1);
        editTextCity = findViewById(R.id.editTextCity);
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextStreetNumber = findViewById(R.id.editTextStreetNumber);
        editTextDescription = findViewById(R.id.editTextDescription);
        uploadPic = findViewById(R.id.uploadPic);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(this::add_parking);
        storageReference=firebaseStorage.getInstance().getReference("parking image");
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextEndDate.setOnClickListener(this::picker2);
        editTextDailyPrice = findViewById(R.id.editTextDailyPrice);

        mAuth=FirebaseAuth.getInstance();

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
                startActivity(new Intent(addParking.this,MainPage.class));
            case R.id.nav_profile:
                startActivity(new Intent(addParking.this,ProfileActivity.class));

            case R.id.nav_addPark:
                break;

            case R.id.nav_MyPark:
                startActivity(new Intent(addParking.this,edit_park.class));

            case R.id.nav_logout:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void picker1(View view) {
        Calendar c= Calendar.getInstance();
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH);
        day=c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog= new DatePickerDialog(addParking.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        finalYear= year;
                        finalMonth=month;
                        finalDay=dayOfMonth;

                        Calendar c= Calendar.getInstance();
                        hour=c.get(Calendar.HOUR_OF_DAY);
                        minute=c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(addParking.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                finalHour=hourOfDay;
                                finalMinute=minute;
                                String dateTime=finalDay+"/"+finalMonth+"/"+finalYear +" "+finalHour+":"+finalMinute;
                                editTextDateTime.setText(dateTime);
                            }
                        },
                                hour, minute, android.text.format.DateFormat.is24HourFormat(addParking.this));
                        timePickerDialog.show();

                    }},year,month,day);
        datePickerDialog.show();
    }

    public void picker2(View view) {
        Calendar c= Calendar.getInstance();
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH);
        day=c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog= new DatePickerDialog(addParking.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        finalYear= year;
                        finalMonth=month;
                        finalDay=dayOfMonth;

                        Calendar c= Calendar.getInstance();
                        hour=c.get(Calendar.HOUR_OF_DAY);
                        minute=c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(addParking.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                finalHour=hourOfDay;
                                finalMinute=minute;
                                String dateTime=finalDay+"/"+finalMonth+"/"+finalYear +" "+finalHour+":"+finalMinute;
                                editTextEndDate.setText(dateTime);
                            }
                        },
                                hour, minute, android.text.format.DateFormat.is24HourFormat(addParking.this));
                        timePickerDialog.show();

                    }},year,month,day);
                datePickerDialog.show();
    }

    public long dateToLong(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        //Date date1 = simpleDateFormat.parse(date);
       // simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
        Date date1 = simpleDateFormat.parse(date);
        long dateInLong = date1.getTime();
       // String value = simpleDateFormat.format(date);
       // Long result = Long.parseLong(value);
        System.out.println("Date         = " + date1);
        System.out.println("Date in Long = " + dateInLong);
       // System.out.println("Result : "+result);
        return dateInLong;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        finalYear= year;
        finalMonth=month;
        finalDay=dayOfMonth;

        Calendar c= Calendar.getInstance();
        hour=c.get(Calendar.HOUR_OF_DAY);
        minute=c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(addParking.this,addParking.this,
                hour,minute, android.text.format.DateFormat.is24HourFormat(this));
        timePickerDialog.show();


    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        finalHour=hourOfDay;
        finalMinute=minute;
       String dateTime=finalDay+"/"+finalMonth+"/"+finalYear +" "+finalHour+":"+finalMinute;
        editTextDateTime.setText(dateTime);

    }

    public void upload_parking(View view) {



        final StorageReference reference=storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
        uploadTask=reference.putFile(imageUri);

        Task<Uri>uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
               if(!task.isSuccessful()){
                   throw task.getException();
               }

                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri=task.getResult();
                    Map<String, Object> parking = new HashMap<>();
                    parking.put(KEY_city, editTextCity.getText().toString());
                    parking.put(KEY_street, editTextStreet.getText().toString());
                    parking.put(KEY_street_number, editTextStreetNumber.getText().toString());
                    parking.put(description, editTextDescription.getText().toString());
                    parking.put(start_date, editTextDateTime.getText().toString());
                    parking.put(end_date, editTextEndDate.getText().toString());
                    parking.put(URI, downloadUri.toString());

                    db.collection("User")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Parking").add(parking)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(addParking.this,"error",Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(addParking.this,"parking created",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }


    
    public void add_parking(View view) {



        final StorageReference reference=storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
        uploadTask=reference.putFile(imageUri);

        Task<Uri>uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }

                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){

                    Uri downloadUri=task.getResult();
                    Map<String, Object> parking = new HashMap<>();
                    parking.put(KEY_city, editTextCity.getText().toString());
                    parking.put(KEY_street, editTextStreet.getText().toString());
                    parking.put(KEY_street_number, editTextStreetNumber.getText().toString());
                    parking.put(KEY_daily_price,editTextDailyPrice.getText().toString());
                    parking.put(description, editTextDescription.getText().toString());
                    parking.put(start_date, editTextDateTime.getText().toString());
                    parking.put(end_date, editTextEndDate.getText().toString());
                    parking.put(userId, mAuth.getCurrentUser().getUid());
                    parking.put(URI, downloadUri.toString());
                   // try {
                       // long_start=dateToLong(editTextDateTime.getText().toString());
                 //   } catch (Exception e) {
                    //    e.printStackTrace();
                  //  }

                    db.collection("ParkingSpot")
                            .add(parking)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(addParking.this,"parking created",Toast.LENGTH_SHORT).show();
                                    String parkingId = documentReference.getId();
                                    //add the parkingID to the user parking array.
                                    DocumentReference userRef = db.collection("User").document(mAuth.getCurrentUser().getUid());
                                    userRef.update("parking spots", FieldValue.arrayUnion(parkingId))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(addParking.this,"error",Toast.LENGTH_SHORT).show();
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(addParking.this,"error",Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }

    public void chooseIMG(View view) {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE||requestCode==RESULT_OK || data!=null ||data.getData()!=null){
            imageUri=data.getData();

            Picasso.with(this).load(imageUri).into(uploadPic);

        }
    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}