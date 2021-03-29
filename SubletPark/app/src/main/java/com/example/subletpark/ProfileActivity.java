package com.example.subletpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        editTextPassword = findViewById(R.id.editTextPassword3);
        editTextPassword2 = findViewById(R.id.editTextPassword4);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();



            db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                firstName.setText(documentSnapshot.getString("firstname"));
                                lastName.setText(documentSnapshot.getString("lastname"));
                                editTextPhone.setText(documentSnapshot.getString("phone"));
                                editTextEmail.setText(documentSnapshot.getString("email"));

                            }else{
                                Toast.makeText(ProfileActivity.this,"document does not exist",Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());

                        }
                    });




        }


    public void update(View view) {
    }
}





