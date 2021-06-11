package com.example.subletpark;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.SneakyThrows;

public class Edit_park extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> group;
    List<String> park_group;
    List<Parking_Class> user_parking = new ArrayList<Parking_Class>();
    EditText editTextaddress1;

    EditText editTextDescription1;
    EditText editTextDailyPrice1;
    ImageView uploadPic1;
    Button buttonUpload1;
    private Uri imageUri1;
    EditText editTextDateTime1;
    EditText editTextEndDate1;
    int day, month, year, hour, minute;
    int finalDay, finalMonth, finalYear, finalHour, finalMinute;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    UploadTask uploadTask;
    String string_start;
    String string_end;
    List<Address> addressList = null;
    TextView noParking;
    CardView cardP;
    TextView deleteP;
    Snackbar snackbar;
    ProgressBar progressBar2;


    Long long_start;
    Long long_finish;

    FirebaseAuth mAuth;


    Uri result = null;


    private static final String TAG = "edit_park";
    private static final int PICK_IMAGE = 1;
    private static final int PICK_PLACE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_park);

        drawerLayout = findViewById(R.id.activity_edit_park);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_MyPark);

        deleteP=findViewById(R.id.deleteP);
        progressBar2=findViewById(R.id.progressBar2);

        editTextaddress1 = findViewById(R.id.editTextaddress1);
        editTextDateTime1 = findViewById(R.id.editTextDateTime1);
        editTextEndDate1 = findViewById(R.id.editTextEndDate1);
        editTextDailyPrice1 = findViewById(R.id.editTextDailyPrice1);
        editTextDescription1 = findViewById(R.id.editTextDescription1);
        uploadPic1 = findViewById(R.id.uploadPic1);
        mAuth = FirebaseAuth.getInstance();
        buttonUpload1 = findViewById(R.id.buttonUpload1);
        noParking = findViewById(R.id.noParking);
        cardP = findViewById(R.id.cardP);
        buttonUpload1.setOnClickListener(this::update_parking);
        storageReference = firebaseStorage.getInstance().getReference("parking image");
        Places.initialize(getApplicationContext(), "AIzaSyDC8wMP9MaCDDnTmdWeXx1-npixfiQiUug", Locale.forLanguageTag("iw"));

        PlacesClient placesClient = Places.createClient(this);


        deleteP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder= new AlertDialog.Builder(Edit_park.this);
                builder.setCancelable(true);
                builder.setTitle("האם אתה בטוח שאתה רוצה למחוק את החניה?");
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

                        delete_parking();

                    }
                });
                builder.show();


            }
        });



        editTextaddress1.setFocusable(false);

        editTextaddress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setTypeFilter(TypeFilter.ADDRESS).build(Edit_park.this);
                startActivityForResult(intent, PICK_PLACE);

            }
        });


        db.collection("User")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.get("parking spots") == null) {
                    noParking.setVisibility(View.VISIBLE);
                    cardP.setVisibility(View.GONE);
                    deleteP.setVisibility(View.GONE);

                } else {
                    group = (List<String>) document.get("parking spots");
                    // for (String parking_spot:group) {
                    Log.d(TAG, "onComplete: " + group.get(group.size() - 1));
                    String curr = group.get(group.size() - 1);
                    db.collection("ParkingSpot").whereEqualTo(FieldPath.documentId(), curr).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @SneakyThrows
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d(TAG, "onComplete: " + queryDocumentSnapshots.getDocuments().get(0).get("address").toString());


                            String address = queryDocumentSnapshots.getDocuments().get(0).get("address").toString();
                            String price = queryDocumentSnapshots.getDocuments().get(0).get("daily price").toString();
                            String start_date = queryDocumentSnapshots.getDocuments().get(0).get("start_date").toString();
                            String end_date = queryDocumentSnapshots.getDocuments().get(0).get("end_date").toString();
                            String desc = queryDocumentSnapshots.getDocuments().get(0).get("description").toString();
                            String uri = queryDocumentSnapshots.getDocuments().get(0).get("uri").toString();




                            try {
                                string_start = longToDate(start_date);
                                string_end = longToDate(end_date);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            imageUri1 = Uri.parse(uri);
                            editTextaddress1.setText(address);
                            editTextDailyPrice1.setText(price);
                            editTextDateTime1.setText(string_start);
                            editTextEndDate1.setText(string_end);
                            editTextDescription1.setText(desc);
                            Picasso.with(getApplicationContext()).load(imageUri1).into(uploadPic1);
                            //uploadPic1.setImageURI(imageUri1);


                            user_parking.add(new Parking_Class(group.get(group.size() - 1), address, price, start_date, end_date, uri, desc));


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            View view = findViewById(R.id.activity_edit_park);
                            snackbar=snackbar.make(view,"נסה שנית",Snackbar.LENGTH_INDEFINITE);
                            snackbar.setDuration(5000);
                            snackbar.setBackgroundTint(Color.rgb(166, 33, 18));
                            snackbar.show();
                        }
                    });
                }


            }
        });


    }



    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            startActivity(new Intent(Edit_park.this, MainPage.class));

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
                startActivity(new Intent(getApplicationContext(), AddParking.class));

                break;

            case R.id.nav_MyPark:

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

    public long dateToLong(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date1 = simpleDateFormat.parse(date);
        long dateInLong = date1.getTime();
        //check only:
        System.out.println("Date         = " + date1);
        System.out.println("Date in Long = " + dateInLong);
        return dateInLong;
    }

    public String longToDate(String date1) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        long milliSeconds = Long.parseLong(date1);
        Date date = new Date(milliSeconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        System.out.println(formatter.format(calendar.getTime()));
        System.out.println(formatter.format(date));

        return formatter.format(date);
    }


    public void chooseIMG(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {

            try {

            if (resultCode == RESULT_OK || data != null || data.getData() != null) {

                imageUri1 = data.getData();
                Picasso.with(this).load(imageUri1).into(uploadPic1);


            }} catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"נא וודא שבחרת תמונה",Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == PICK_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                editTextaddress1.setText(place.getAddress());
            }


        } else if (requestCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void update_parking(View view) {
        progressBar2.setVisibility(View.VISIBLE);

        // if contains https:// ignore uploadTask
        if (!imageUri1.getScheme().contains("https")) {
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri1));

            uploadTask = reference.putFile(imageUri1);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    result = task.getResult();
                    updateParking();
                }
            });
        } else {
            updateParking();
        }
    }

    private void updateParking() {

        if(editTextaddress1.getText().toString().isEmpty()){

            editTextaddress1.setError("נא להזין כתובת");
            editTextaddress1.requestFocus();
            progressBar2.setVisibility(View.GONE);
            return;

        }

        if(editTextDateTime1.getText().toString().isEmpty()){

            editTextDateTime1.setError("נא להזין מועד תחילת השכרה");
            editTextDateTime1.requestFocus();
            progressBar2.setVisibility(View.GONE);
            return;

        }
        if(editTextEndDate1.getText().toString().isEmpty()){

            editTextEndDate1.setError("נא להזין מועד סיום השכרה");
            editTextEndDate1.requestFocus();
            progressBar2.setVisibility(View.GONE);
            return;

        }

        if(editTextDailyPrice1.getText().toString().isEmpty()){

            editTextDailyPrice1.setError("נא הזן מחיר יומי בשקלים");
            editTextDailyPrice1.requestFocus();
            progressBar2.setVisibility(View.GONE);
            return;

        }

        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = formatter.parse(editTextEndDate1.getText().toString());

        } catch (ParseException ex) {
            ex.printStackTrace();
            editTextEndDate1.setError("נא להזין מועד בפורמט : dd/MM/yyyy HH:mm ");
            editTextEndDate1.requestFocus();
            progressBar2.setVisibility(View.GONE);
            return;

        }

        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = formatter.parse(editTextDateTime1.getText().toString());

        } catch (ParseException ex) {
            ex.printStackTrace();
            editTextDateTime1.setError("נא להזין מועד בפורמט : dd/MM/yyyy HH:mm ");
            editTextDateTime1.requestFocus();
            progressBar2.setVisibility(View.GONE);
            return;

        }


        try {
            long_start = dateToLong(editTextDateTime1.getText().toString());
            long_finish = dateToLong(editTextEndDate1.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri downloadUri = result == null ? imageUri1 : result;
        result = null;


        if (long_start > long_finish) {
            editTextEndDate1.setError("נא לבחור תאריך סיום חניה מאוחר מתאריך תחילת חניה");
            editTextEndDate1.requestFocus();
            return;
        }

        db.collection("ParkingSpot")
                .document(group.get(group.size() - 1)).update("address", editTextaddress1.getText().toString(),
                "daily price", editTextDailyPrice1.getText().toString(), "description", editTextDescription1.getText().toString()
                , "start_date", long_start, "end_date", long_finish, "uri", downloadUri.toString(), "lat", addressToLatLng().latitude, "lng", addressToLatLng().longitude).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                View view = findViewById(R.id.activity_edit_park);
                snackbar=snackbar.make(view,"נסה שנית",Snackbar.LENGTH_INDEFINITE);
                snackbar.setDuration(5000);
                snackbar.setBackgroundTint(Color.rgb(166, 33, 18));
                snackbar.show();
                Log.d(TAG, e.toString());
                progressBar2.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                View view= findViewById(R.id.activity_edit_park);
                snackbar=snackbar.make(view,"החניה שלך עודכנה בהצלחה!",Snackbar.LENGTH_INDEFINITE);
                snackbar.setDuration(5000);
                snackbar.setBackgroundTint(Color.rgb(13, 130, 101));
                snackbar.show();
                progressBar2.setVisibility(View.GONE);


            }
        });
    }

    public LatLng addressToLatLng() {

        String location = editTextaddress1.getText().toString();


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

    public void picker1(View view) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Edit_park.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        finalYear = year;
                        finalMonth = month + 1;
                        finalDay = dayOfMonth;

                        Calendar c = Calendar.getInstance();
                        hour = c.get(Calendar.HOUR_OF_DAY);
                        minute = c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(Edit_park.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                finalHour = hourOfDay;
                                finalMinute = minute;
                                String dateTime = finalDay + "/" + finalMonth + "/" + finalYear + " ";
                                editTextDateTime1.setText(dateTime+(String.format("%02d:%02d",finalHour,finalMinute)));
                            }
                        },
                                hour, minute, android.text.format.DateFormat.is24HourFormat(Edit_park.this));
                        timePickerDialog.show();

                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public void picker2(View view) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Edit_park.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        finalYear = year;
                        finalMonth = month + 1;
                        finalDay = dayOfMonth;

                        Calendar c = Calendar.getInstance();
                        hour = c.get(Calendar.HOUR_OF_DAY);
                        minute = c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(Edit_park.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                finalHour = hourOfDay;
                                finalMinute = minute;
                                String dateTime = finalDay + "/" + finalMonth + "/" + finalYear + " ";
                                editTextEndDate1.setText(dateTime+(String.format("%02d:%02d",finalHour,finalMinute)));
                            }
                        },
                                hour, minute, android.text.format.DateFormat.is24HourFormat(Edit_park.this));
                        timePickerDialog.show();

                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        finalYear = year;
        finalMonth = month;
        finalDay = dayOfMonth;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Edit_park.this, Edit_park.this,
                hour, minute, android.text.format.DateFormat.is24HourFormat(this));
        timePickerDialog.show();


    }

    //  @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        finalHour = hourOfDay;
        finalMinute = minute;
        String dateTime = finalDay + "/" + finalMonth + "/" + finalYear + " ";
        editTextDateTime1.setText(dateTime+(String.format("%02d:%02d",finalHour,finalMinute)));

    }

    public void delete_parking() {

        db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                park_group = (List<String>) document.get("parking spots");

                if (document.get("parking spots") != null) {
                    String curr1 = park_group.get(park_group.size() - 1);
                    db.collection("ParkingSpot").document(curr1).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.d(TAG, "Parking successfully deleted!");
                        }


                    });
                }
                park_group.remove(park_group.size() - 1);
                Map<String, Object> updates = new HashMap<>();
                updates.put("parking spots", FieldValue.delete());
                if (park_group.size() > 0) {
                    updates.put("parking spots", park_group);
                }

                db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Parking array successfully updated!");
                        startActivity(new Intent(Edit_park.this, Edit_park.class));

                    }
                });
            }
        });


    }
}