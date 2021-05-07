package com.example.subletpark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

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

    public void signinFacebook(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile","mobile_phone"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());

                //String lineOfCurrencies =firebaseUser.getDisplayName();
               // String[] currencies = lineOfCurrencies.split(" ");
               // String first_name = currencies[0];
               // String last_name = currencies[1];

               /** AccessToken accessToken = loginResult.getAccessToken();
                AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken());
                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            updateUI(firebaseUser);
                            setFacebookData(loginResult);

                        }else {
                            Toast.makeText(login.this,"could not register, please try again",Toast.LENGTH_LONG).show();
                        }

                    }
                });**/


            }

            @Override
            public void onCancel() {
                Toast.makeText(login.this,"registration cancelled",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(login.this,error.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }

   private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    updateUI(firebaseUser);
                    Profile profile = Profile.getCurrentProfile();
                    if(profile!=null){
                        first_name=profile.getFirstName();
                        last_name=profile.getLastName();

                        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    phone=object.getString("mobile_phone");
                                    email=object.getString("email");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,email,first_name,last_name,mobile_phone");
                        request.setParameters(parameters);
                        request.executeAsync();

                        Map<String, Object> user = new HashMap<>();
                        user.put("uid", mAuth.getCurrentUser().getUid());
                        user.put("lastLoginDate", Calendar.getInstance().getTime());
                        user.put(KEY_name, first_name);
                        user.put(KEY_last_name, last_name);
                        user.put(KEY_phone, phone);
                        user.put(KEY_email, email);
                        db1.collection("User").document(user.get("uid").toString())
                                .set(user)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(login.this,"error",Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(login.this,"user created",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                }else {
                    Toast.makeText(login.this,"could not register, please try again",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

  private void setFacebookData(final LoginResult loginResult) {
      GraphRequest request = GraphRequest.newMeRequest(
              loginResult.getAccessToken(),
              new GraphRequest.GraphJSONObjectCallback() {
                  @Override
                  public void onCompleted(JSONObject object, GraphResponse response) {
                      try {
                          //first_name = response.getJSONObject().getString("first_name");
                         // last_name = response.getJSONObject().getString("last_name");
                          last_name=object.getString("last_name");
                          first_name=object.getString("first_name");
                          phone=object.getString("phone");
                          email=object.getString("email");
                         /// phone = response.getJSONObject().getString("phone");
                         // if (response.getJSONObject().has("email"))
                             // email = response.getJSONObject().getString("email");
                          //put your code here
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }
                  }
              });
      Bundle parameters = new Bundle();
      parameters.putString("fields", "id,email,first_name,last_name,phone");
      request.setParameters(parameters);
      request.executeAsync();

      Map<String, Object> user = new HashMap<>();
      user.put("uid", mAuth.getCurrentUser().getUid());
      user.put("lastLoginDate", Calendar.getInstance().getTime());
      user.put(KEY_name, first_name);
      user.put(KEY_last_name, last_name);
      user.put(KEY_phone, phone);
      user.put(KEY_email, email);
      db1.collection("User").document(user.get("uid").toString())
              .set(user)
              .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Toast.makeText(login.this,"error",Toast.LENGTH_SHORT).show();
                      Log.d(TAG, e.toString());
                  }
              }).addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
              Toast.makeText(login.this,"user created",Toast.LENGTH_SHORT).show();
          }
      });
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

            loginPassword.setError("password is required!");
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
                    startActivity(new Intent(login.this, MainPage.class));
                  // finish();


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