package com.example.subletpark;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addParking extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, NavigationView.OnNavigationItemSelectedListener {

    TextView editTextDateTime;
    int day,month,year, hour,minute;
    int finalDay,finalMonth,finalYear,finalHour,finalMinute;

    EditText editTextaddress;
    //EditText editTextStreet;
    //EditText editTextStreetNumber;
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
    Long long_finish;
    List<Address> addressList = null;

    private static final String KEY_address = "address";
    private static final String KEY_street= "street";
    private static final String KEY_street_number = "street_number";
    private static final String KEY_daily_price= "daily price";
    private static final String description = "description";
    private static final String start_date = "start_date";
    private static final String end_date = "end_date";
    private static final String lat = "lat";
    private static final String lng = "lng";
    private static final String userId = "userId";
    private static final String URI = "uri";
    private static final String TAG ="addParking";
    private static final int PICK_IMAGE=1;
    private static final int PICK_PLACE=2;


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
        editTextaddress = findViewById(R.id.editTextaddress);
        //editTextStreet = findViewById(R.id.editTextStreet);
        //editTextStreetNumber = findViewById(R.id.editTextStreetNumber);
        editTextDescription = findViewById(R.id.editTextDescription);
        uploadPic = findViewById(R.id.uploadPic);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(this::add_parking);
        storageReference=firebaseStorage.getInstance().getReference("parking image");
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextEndDate.setOnClickListener(this::picker2);
        editTextDailyPrice = findViewById(R.id.editTextDailyPrice);

        mAuth=FirebaseAuth.getInstance();

        Places.initialize(getApplicationContext(),"AIzaSyDC8wMP9MaCDDnTmdWeXx1-npixfiQiUug");

        PlacesClient placesClient = Places.createClient(this);


          editTextaddress.setFocusable(false);

          editTextaddress.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
                List<Place.Field> fieldList= Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).setTypeFilter(TypeFilter.ADDRESS).build(addParking.this);
               startActivityForResult(intent,PICK_PLACE);

           }
        });

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
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), MainPage.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                break;

            case R.id.nav_addPark:

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
        System.out.println("Date         = " + date1);
        System.out.println("Date in Long = " + dateInLong);
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

   /** public void upload_parking(View view) {



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


    **/
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
                    try {
                        long_start=dateToLong(editTextDateTime.getText().toString());
                        long_finish= dateToLong(editTextEndDate.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Uri downloadUri=task.getResult();
                    Map<String, Object> parking = new HashMap<>();
                    parking.put(KEY_address, editTextaddress.getText().toString());
                   // parking.put(KEY_street, editTextStreet.getText().toString());
                   // parking.put(KEY_street_number, editTextStreetNumber.getText().toString());
                    parking.put(KEY_daily_price,editTextDailyPrice.getText().toString());
                    parking.put(description, editTextDescription.getText().toString());
                    parking.put(lat, addressToLatLng().latitude);
                    parking.put(lng, addressToLatLng().longitude);
                    parking.put(start_date, long_start);
                    parking.put(end_date, long_finish);
                    parking.put(userId, mAuth.getCurrentUser().getUid());
                    parking.put(URI, downloadUri.toString());

                    if (long_start>long_finish){
                        editTextDateTime.setError("נא לבחור תאריך סיום חניה מאוחר מתאריך תחילת חניה");
                        editTextDateTime.requestFocus();
                        return;
                    }

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

    public LatLng addressToLatLng() {

        //String location = editTextStreet.getText().toString()+" "+editTextStreetNumber.getText().toString()+" "+editTextCity.getText().toString();
        String location = editTextaddress.getText().toString();


        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

          return latLng;


        }
        return null;
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

        if(requestCode==PICK_IMAGE){
            if(resultCode==RESULT_OK || data!=null ||data.getData()!=null) {
                imageUri = data.getData();

                Picasso.with(this).load(imageUri).into(uploadPic);
            }

        }
        else if (requestCode==PICK_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                editTextaddress.setText(place.getAddress());
            }


        }else if(requestCode== AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_LONG).show();
        }

    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}