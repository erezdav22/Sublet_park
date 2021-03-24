package com.example.subletpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText UserEmail;
    private Button resetButton;
    private ProgressBar progressBar;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        UserEmail=findViewById(R.id.UserEmail);
        resetButton=findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this::reset);
        progressBar=findViewById(R.id.progressBar);

        auth=FirebaseAuth.getInstance();

    }

    public void reset(View view) {
        String email=UserEmail.getText().toString().trim();
        if(email.isEmpty()){

            UserEmail.setError("email is required!");
            UserEmail.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            UserEmail.setError("please provide valid email");
            UserEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ResetPassword.this,"check your email to reset password",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ResetPassword.this,login.class));


                }else{
                    Toast.makeText(ResetPassword.this,"please try again",Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}