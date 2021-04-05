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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private TextView textViewRegister,forgotPassword;
    private EditText loginEmail,loginPassword;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


    }

    public void login(View view) {
        String email=loginEmail.getText().toString().trim();
        String password=loginPassword.getText().toString().trim();

        if(email.isEmpty()){

            loginEmail.setError("email is required!");
            loginEmail.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("please provide valid email");
            loginEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){

            loginPassword.setError("email is required!");
            loginPassword.requestFocus();
            return;

        }
        if(password.length()<6){
            loginPassword.setError("min password length should be 6 characters");
            loginPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(login.this,MainPage.class));



                }else{
                    Toast.makeText(login.this,"Failed to login, please try again",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void register(View view) {
        startActivity(new Intent(login.this,Registration.class));
    }

    public void resetPass(View view) {

        startActivity(new Intent(login.this,ResetPassword.class));
    }
}