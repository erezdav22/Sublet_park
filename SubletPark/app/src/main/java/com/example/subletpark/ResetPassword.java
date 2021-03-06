package com.example.subletpark;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText UserEmail;
    private Button resetButton;
    private ProgressBar progressBar;
    FirebaseAuth auth;
    Snackbar snackbar;


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

            UserEmail.setError("נא להזין כתובת מייל כדי שנדע לאן לשלוח את הלינק");
            UserEmail.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            UserEmail.setError("נא להזין כתובת מייל תקינה");
            UserEmail.requestFocus();
            return;
        }



        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ResetPassword.this,"בדוק את תיבת המייל שלך לעדכון סיסמה חדשה", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(ResetPassword.this, Login.class));


                }else{

                    snackbar=snackbar.make(view,"נסה שנית",Snackbar.LENGTH_INDEFINITE);
                    snackbar.setDuration(5000);
                    snackbar.setBackgroundTint(Color.rgb(166, 33, 18));
                    snackbar.show();
                }
            }
        });

    }
}