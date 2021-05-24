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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private TextView textViewRegister,forgotPassword;
    private EditText loginEmail,loginPassword;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore db1=FirebaseFirestore.getInstance();
    private static final String TAG ="login";


    LoginButton facebookLogin;
    CallbackManager callbackManager;
    private static final String KEY_name = "firstname";
    private static final String KEY_last_name = "lastname";
    private static final String KEY_phone = "phone";
    private static final String KEY_email = "email";
    String phone;
    String email;
    String first_name;
    String last_name;
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


        //facebook sign in

        callbackManager = CallbackManager.Factory.create();

        AppEventsLogger.activateApp(getApplication());
//     facebookLogin.setPermissions(Arrays.asList("email"));


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


    }



    public void updateUI(FirebaseUser currentUser){
        Intent loginIntent= new Intent(this,MainPage.class);
        startActivity(loginIntent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
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
                  // finish();


                }else{
                   // Toast.makeText(Login.this,"נראה שאחד מפרטי ההתחברות שלך לא נכונים, שננסה שוב?",Toast.LENGTH_LONG).show();
                    //View view= findViewById(R.id.content);
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