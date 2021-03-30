package com.example.subletpark;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private EditText firstName;
    private EditText lastName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPassword2;
    String firstname;
    String lastname;
    String phone;
    String email;
    String uid;
    private static final String TAG = "ProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = findViewById(R.id.firstName2);
        lastName = findViewById(R.id.lastName2);
        editTextPhone = findViewById(R.id.editTextPhone3);
        editTextEmail = findViewById(R.id.editTextEmail2);
        // editTextPassword = findViewById(R.id.editTextPassword3);
        //editTextPassword2 = findViewById(R.id.editTextPassword4);


//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        uid=user.getUid();


        db.collection("User")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        System.out.println("im here");
                        if (!querySnapshot.isEmpty()) {
                            System.out.println(querySnapshot.getDocuments().get(0).get("lastLoginDate"));
                                firstName.setText(querySnapshot.getDocuments().get(0).get("firstname").toString());
                                lastName.setText(querySnapshot.getDocuments().get(0).get("lastname").toString());
                                editTextPhone.setText(querySnapshot.getDocuments().get(0).get("phone").toString());
                                editTextEmail.setText(querySnapshot.getDocuments().get(0).get("email").toString());

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


    public void update(View view) {

        db.collection("User")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("phone","123456789");
    }
}





