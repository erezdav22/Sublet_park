package com.example.subletpark;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextView textViewRegister,forgotPassword;
    private EditText loginEmail,loginPassword;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginEmail=findViewById(R.id.loginEmail);
        loginPassword=findViewById(R.id.loginPassword);
        loginButton=findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this::login);


        progressBar=findViewById(R.id.progressBar);
        mAuth=FirebaseAuth.getInstance();

        textViewRegister=findViewById(R.id.textViewRegister);
        forgotPassword=findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this::resetPass);
        textViewRegister.setOnClickListener(this::register);


    }



    public void updateUI(FirebaseUser currentUser){
        Intent loginIntent= new Intent(this,MainPage.class);
        startActivity(loginIntent);

    }

    public void login(View view) {
        String email=loginEmail.getText().toString().trim();
        String password=loginPassword.getText().toString().trim();

        if(email.isEmpty()){

            loginEmail.setError("נא להזין כתובת מייל");
            loginEmail.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("נא להזין כתובת מייל תקינה");
            loginEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){

            loginPassword.setError("נא להזין סיסמה");
            loginPassword.requestFocus();
            return;

        }
        if(password.length()<8){
            loginPassword.setError("נא להזין סיסמה באורך 8 תווים לפחות");
            loginPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(Login.this, MainPage.class));



                }else{

                    snackbar=snackbar.make(view,"נראה שאחד מפרטי ההתחברות שלך לא נכונים, שננסה שוב?",Snackbar.LENGTH_INDEFINITE);
                    snackbar.setDuration(5000);
                    snackbar.setBackgroundTint(Color.rgb(166, 33, 18));
                    snackbar.show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void register(View view) {
        startActivity(new Intent(Login.this,Registration.class));
    }

    public void resetPass(View view) {

        startActivity(new Intent(Login.this,ResetPassword.class));
    }


}