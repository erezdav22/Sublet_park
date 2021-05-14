package com.example.subletpark;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPassword2;
    private Button register;
    private FirebaseFirestore db1=FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private static final String USER ="user";
    private static final String TAG ="registration";


    private static final String KEY_name = "firstname";
    private static final String KEY_lastName= "lastname";
    private static final String KEY_phone = "phone";
    private static final String KEY_email = "email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firstName=findViewById(R.id.firstName);
        lastName=findViewById(R.id.lastName);
        editTextPhone=findViewById(R.id.editTextPhone);
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        editTextPassword2=findViewById(R.id.editTextPassword2);
        register=findViewById(R.id.register);
        register.setOnClickListener(this::sign_up);


        mAuth=FirebaseAuth.getInstance();





    }


        public void sign_up (View view){

        String first_name=firstName.getText().toString();
        String last_name=lastName.getText().toString();
        String phone=editTextPhone.getText().toString();
        String email=editTextEmail.getText().toString();
        String password=editTextPassword.getText().toString();
        String password2=editTextPassword2.getText().toString();

        if(first_name.isEmpty()){

            firstName.setError("first name is required!");
            firstName.requestFocus();
            return;

        }



        if(!Pattern.matches("[a-zA-Z\\u0590-\\u05FF]+",first_name)){

            firstName.setError("only letters!");
            firstName.requestFocus();
            return;

        }



        if(last_name.isEmpty()){

                lastName.setError("last name is required!");
                lastName.requestFocus();
                return;

        }

            if(!Pattern.matches("[a-zA-Z\\u0590-\\u05FF]+",last_name)){

                lastName.setError("only letters!");
                lastName.requestFocus();
                return;

            }

        if(phone.isEmpty()){

            editTextPhone.setError("phone number is required!");
            editTextPhone.requestFocus();
                return;

        }

            if(phone.length()<10){

                editTextPhone.setError("phone number is not valid!");
                editTextPhone.requestFocus();
                return;

            }

        if(email.isEmpty()){

            editTextEmail.setError("email is required!");
            editTextEmail.requestFocus();
                return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("please provide valid email");
            editTextEmail.requestFocus();
            return;
        }

            if(password.isEmpty()){

                editTextPassword.setError("email is required!");
                editTextPassword.requestFocus();
                return;

            }


            if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",password)){

                editTextPassword.setError("please useat minimum 8 characters, at least one letter and one number in your password!");
                editTextPassword.requestFocus();
                return;

            }

            if(!password.equals(password2)||password2.isEmpty()){
                editTextPassword2.setError("please enter same password twice");
                editTextPassword2.requestFocus();
                return;
            }


            registration(email,password);






        }

        public void registration(String email, String password) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                saveUserProfile();
                                Toast.makeText(Registration.this, "נרשמת בהצלחה",
                                        Toast.LENGTH_LONG).show();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Registration.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    public void saveUserProfile() {
        Map<String, Object> user = new HashMap<>();
        user.put("uid", mAuth.getCurrentUser().getUid());
        user.put("lastLoginDate", Calendar.getInstance().getTime());
        user.put(KEY_name, firstName.getText().toString());
        user.put(KEY_lastName, lastName.getText().toString());
        user.put(KEY_phone, editTextPhone.getText().toString());
        user.put(KEY_email, editTextEmail.getText().toString());
        db1.collection("User").document(user.get("uid").toString())
                .set(user)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Registration.this,"error",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

    }

       public void updateUI(FirebaseUser currentUser){
            Intent loginIntent= new Intent(this,login.class);
            startActivity(loginIntent);

        }


}