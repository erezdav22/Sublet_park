package com.example.subletpark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URI;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class addParking extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView editTextDateTime;
    int day,month,year, hour,minute;
    int finalDay,finalMonth,finalYear,finalHour,finalMinute;

    EditText editTextCity;
    EditText editTextStreet;
    EditText editTextStreetNumber;
    EditText editTextDescription;
    ImageView uploadPic;
    Button buttonUpload;
    private Uri imageUri;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    UploadTask uploadTask;
   
    private static final String KEY_city = "city";
    private static final String KEY_street= "street";
    private static final String KEY_street_number = "street_number";
    private static final String description = "description";
    private static final String start_date = "start_date";
    private static final String URI = "uri";
    private static final String TAG ="addParking";
    private static final int PICK_IMAGE=1;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking);

        editTextDateTime = findViewById(R.id.editTextDateTime);
        editTextDateTime.setOnClickListener(this::picker);
        editTextCity = findViewById(R.id.editTextCity);
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextStreetNumber = findViewById(R.id.editTextStreetNumber);
        editTextDescription = findViewById(R.id.editTextDescription);
        uploadPic = findViewById(R.id.uploadPic);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(this::upload_parking);
        storageReference=firebaseStorage.getInstance().getReference("parking image");

    }

    public void picker(View view) {
        Calendar c= Calendar.getInstance();
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH);
        day=c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog= new DatePickerDialog(addParking.this,addParking.this,
                year,month,day);
        datePickerDialog.show();

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