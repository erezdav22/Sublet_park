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
import android.content.Intent;
import lombok.Data;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class edit_park extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener,NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int num=0;
    private int numid=0;
    TableLayout table;
    String parkid;
    List<String> group;
    ListView list;
    List<Parking_Class> user_parking=new ArrayList<Parking_Class>();
    EditText editTextCity1;
    EditText editTextStreet1;
    EditText editTextStreetNumber1;
    EditText editTextDescription1;
    EditText editTextDailyPrice1;
    ImageView uploadPic1;
    Button buttonUpload1;
    private Uri imageUri1;
    TextView editTextDateTime1;
    TextView editTextEndDate1;
    int day,month,year, hour,minute;
    int finalDay,finalMonth,finalYear,finalHour,finalMinute;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    UploadTask uploadTask;


    Long long_start;
    Long long_finish;

    FirebaseAuth mAuth;

    private static final String KEY_city = "city";
    private static final String KEY_street= "street";
    private static final String KEY_street_number = "street_number";
    private static final String KEY_daily_price= "daily price";
    private static final String description = "description";
    private static final String start_date = "start_date";
    private static final String end_date = "end_date";
    private static final String userId = "userId";
    private static final String URI = "uri";
    private static final String TAG ="edit_park";
    private static final int PICK_IMAGE=1;




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

        editTextCity1 = findViewById(R.id.editTextCity1);
        editTextStreet1 = findViewById(R.id.editTextStreet1);
        editTextStreetNumber1 = findViewById(R.id.editTextStreetNumber1);
        editTextDateTime1 = findViewById(R.id.editTextDateTime1);
        editTextEndDate1 = findViewById(R.id.editTextEndDate1);
        editTextDailyPrice1 = findViewById(R.id.editTextDailyPrice1);
        editTextDescription1 = findViewById(R.id.editTextDescription1);
        uploadPic1 = findViewById(R.id.uploadPic1);
        mAuth=FirebaseAuth.getInstance();
        buttonUpload1 = findViewById(R.id.buttonUpload1);
        buttonUpload1.setOnClickListener(this::update_parking);
        storageReference=firebaseStorage.getInstance().getReference("parking image");


        db.collection("User")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                group = (List<String>) document.get("parking spots");
              // for (String parking_spot:group) {
                    Log.d(TAG, "onComplete: "+ group.get(group.size()-1));
                    String curr= group.get(group.size()-1);
                    db.collection("ParkingSpot").whereEqualTo(FieldPath.documentId(), curr).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                            imageUri1=Uri.parse(uri);
                            editTextCity1.setText(city);
                            editTextStreet1.setText(street);
                            editTextStreetNumber1.setText(street_number);
                            editTextDailyPrice1.setText(price);
                            editTextDateTime1.setText(start_date);
                            editTextEndDate1.setText(end_date);
                            editTextDescription1.setText(desc);
                            uploadPic1.setImageURI(imageUri1);

                            //Parking_Class park=new Parking_Class(parking_spot,city,street,street_number,price,start_date,end_date,uri,desc);

//                            user_parking.add(Parking_Class.builder().city(queryDocumentSnapshots.getDocuments().get(0).get("city").toString()).id(parking_spot).
//                                    street(queryDocumentSnapshots.getDocuments().get(0).get("street").toString()).street_num(queryDocumentSnapshots.getDocuments().get(0).get("street_number").toString()).
//                                    price(queryDocumentSnapshots.getDocuments().get(0).get("daily price").toString()).start_date(queryDocumentSnapshots.getDocuments().get(0).get("start_date").toString()).
//                                    end_date(queryDocumentSnapshots.getDocuments().get(0).get("end_date").toString()).desc(queryDocumentSnapshots.getDocuments().get(0).get("description").toString()).
//                                    uri(queryDocumentSnapshots.getDocuments().get(0).get("uri").toString()).build());


                            user_parking.add(new Parking_Class( group.get(group.size()-1),city,street,street_number,price,start_date,end_date,uri,desc));

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
        });



      /**  ArrayAdapter<Parking_Class> myAdapter= new ArrayAdapter<Parking_Class>(this, android.R.layout.simple_list_item_1,user_parking);

        list.setAdapter(myAdapter);
        list.setOnItemClickListener(listclick);
        **/
    }

   /** private AdapterView.OnItemClickListener listclick= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            String itemValue=(String) list.getItemAtPosition(position);
            startActivity(new Intent(edit_park.this,addParking.class));

        }
    };**/


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

    public long dateToLong(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date1 = simpleDateFormat.parse(date);
        long dateInLong = date1.getTime();
        //check only:
        System.out.println("Date         = " + date1);
        System.out.println("Date in Long = " + dateInLong);
        return dateInLong;
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
            imageUri1=data.getData();

            Picasso.with(this).load(imageUri1).into(uploadPic1);

        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void update_parking(View view) {

        final StorageReference reference=storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri1));
        uploadTask=reference.putFile(imageUri1);

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
                        long_start=dateToLong(editTextDateTime1.getText().toString());
                        long_finish= dateToLong(editTextEndDate1.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Uri downloadUri=task.getResult();
                    Map<String, Object> parking = new HashMap<>();
                    parking.put(KEY_city, editTextCity1.getText().toString());
                    parking.put(KEY_street, editTextStreet1.getText().toString());
                    parking.put(KEY_street_number, editTextStreetNumber1.getText().toString());
                    parking.put(KEY_daily_price,editTextDailyPrice1.getText().toString());
                    parking.put(description, editTextDescription1.getText().toString());
                    parking.put(start_date, long_start);
                    parking.put(end_date, long_finish);
                    parking.put(userId, mAuth.getCurrentUser().getUid());
                    parking.put(URI, downloadUri.toString());


                    db.collection("ParkingSpot")
                            .add(parking)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(edit_park.this,"parking created",Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(edit_park.this,"error",Toast.LENGTH_SHORT).show();
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(edit_park.this,"error",Toast.LENGTH_SHORT).show();
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

    public void picker1(View view) {
        Calendar c= Calendar.getInstance();
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH);
        day=c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog= new DatePickerDialog(edit_park.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        finalYear= year;
                        finalMonth=month;
                        finalDay=dayOfMonth;

                        Calendar c= Calendar.getInstance();
                        hour=c.get(Calendar.HOUR_OF_DAY);
                        minute=c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(edit_park.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                finalHour=hourOfDay;
                                finalMinute=minute;
                                String dateTime=finalDay+"/"+finalMonth+"/"+finalYear +" "+finalHour+":"+finalMinute;
                                editTextDateTime1.setText(dateTime);
                            }
                        },
                                hour, minute, android.text.format.DateFormat.is24HourFormat(edit_park.this));
                        timePickerDialog.show();

                    }},year,month,day);
        datePickerDialog.show();
    }

    public void picker2(View view) {
        Calendar c= Calendar.getInstance();
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH);
        day=c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog= new DatePickerDialog(edit_park.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        finalYear= year;
                        finalMonth=month;
                        finalDay=dayOfMonth;

                        Calendar c= Calendar.getInstance();
                        hour=c.get(Calendar.HOUR_OF_DAY);
                        minute=c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(edit_park.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                finalHour=hourOfDay;
                                finalMinute=minute;
                                String dateTime=finalDay+"/"+finalMonth+"/"+finalYear +" "+finalHour+":"+finalMinute;
                                editTextEndDate1.setText(dateTime);
                            }
                        },
                                hour, minute, android.text.format.DateFormat.is24HourFormat(edit_park.this));
                        timePickerDialog.show();

                    }},year,month,day);
        datePickerDialog.show();
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        finalYear= year;
        finalMonth=month;
        finalDay=dayOfMonth;

        Calendar c= Calendar.getInstance();
        hour=c.get(Calendar.HOUR_OF_DAY);
        minute=c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(edit_park.this,edit_park.this,
                hour,minute, android.text.format.DateFormat.is24HourFormat(this));
        timePickerDialog.show();


    }

  //  @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        finalHour=hourOfDay;
        finalMinute=minute;
        String dateTime=finalDay+"/"+finalMonth+"/"+finalYear +" "+finalHour+":"+finalMinute;
        editTextDateTime1.setText(dateTime);

    }
}
