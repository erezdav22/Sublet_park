package com.example.subletpark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private EditText firstName2;
    private EditText lastName2;
    private EditText editTextPhone3;
    private EditText editTextEmail2;
    private EditText editTextPassword3;
    private EditText editTextPassword4;
    private Button updateButton;
    private Button ChangeButton;



    private static final String TAG = "ProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName2 = findViewById(R.id.firstName2);
        lastName2 = findViewById(R.id.lastName2);
        editTextPhone3 = findViewById(R.id.editTextPhone3);
        editTextEmail2 = findViewById(R.id.editTextEmail2);
        updateButton=findViewById(R.id.updateButton);
         editTextPassword3 = findViewById(R.id.editTextPassword3);
        editTextPassword4 = findViewById(R.id.editTextPassword4);
        ChangeButton=findViewById(R.id.ChangeButton);
        updateButton.setOnClickListener(this::valid);




        db.collection("User")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                           // System.out.println(querySnapshot.getDocuments().get(0).get("lastLoginDate"));
                                firstName2.setText(querySnapshot.getDocuments().get(0).get("firstname").toString());
                                lastName2.setText(querySnapshot.getDocuments().get(0).get("lastname").toString());
                                editTextPhone3.setText(querySnapshot.getDocuments().get(0).get("phone").toString());
                                editTextEmail2.setText(querySnapshot.getDocuments().get(0).get("email").toString());

                        } else {
                            Toast.makeText(ProfileActivity.this, "document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());

                    }
                });
    }



    public void valid(View view) {

        String phone=editTextPhone3.getText().toString();
        String first_name=firstName2.getText().toString();
        String last_name=lastName2.getText().toString();
        String email=editTextEmail2.getText().toString();


        if(first_name.isEmpty()){

            firstName2.setError("first name is required!");
            firstName2.requestFocus();
            return;

        }



        if(!Pattern.matches("[a-zA-Z\\u0590-\\u05FF]+",first_name)){

            firstName2.setError("only letters!");
            firstName2.requestFocus();
            return;

        }



        if(last_name.isEmpty()){

            lastName2.setError("last name is required!");
            lastName2.requestFocus();
            return;

        }

        if(!Pattern.matches("[a-zA-Z\\u0590-\\u05FF]+",last_name)){

            lastName2.setError("only letters!");
            lastName2.requestFocus();
            return;

        }


        if(phone.isEmpty()){

            editTextPhone3.setError("phone number is required!");
            editTextPhone3.requestFocus();
            return;

        }

        if(phone.length()<10){

            editTextPhone3.setError("phone number is not valid!");
            editTextPhone3.requestFocus();
            return;

        }


        if(email.isEmpty()){

            editTextEmail2.setError("email is required!");
            editTextEmail2.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail2.setError("please provide valid email");
            editTextEmail2.requestFocus();
            return;
        }




        update();

    }

    public void update(){

      //  db.collection("User")
            //    .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("phone",editTextPhone3.getText().toString());
       // db.collection("User")
               // .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("email",editTextEmail2.getText().toString());

            db.collection("User")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("phone",editTextPhone3.getText().toString(),
                    "email",editTextEmail2.getText().toString(),"firstname",firstName2.getText().toString(),
                    "lastname",lastName2.getText().toString()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this,"error",Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProfileActivity.this,"your profile was updated successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProfileActivity.this,addParking.class));


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

            editTextPassword3.setError("email is required!");
            editTextPassword3.requestFocus();
            return;

        }


        if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",password)){

            editTextPassword3.setError("please useat minimum 8 characters, at least one letter and one number in your password!");
            editTextPassword3.requestFocus();
            return;

        }

        if(!password.equals(password2)||password2.isEmpty()){
            editTextPassword4.setError("please enter same password twice");
            editTextPassword4.requestFocus();
            return;
        }

        FirebaseAuth.getInstance().getCurrentUser().updatePassword(editTextPassword3.getText().toString()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this,"error",Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileActivity.this,"your password was updated successfully",Toast.LENGTH_SHORT).show();


            }
        });
    }
}





