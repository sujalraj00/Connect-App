package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connect.Models.Users;
import com.example.connect.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
TextView signin_txt;
CheckBox checkBox1;
EditText edtPassword;
ActivityLoginBinding binding;
FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
ProgressDialog progressDialog;
FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

//        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Loging In");
        progressDialog.setMessage("Login to your account");

        // defining
        signin_txt = findViewById(R.id.signin_txt);
        checkBox1 = findViewById(R.id.checkbox1);
        //edtPassword = findViewById(R.id.edtPassword);

        //  configure google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // intent
        Intent sintent = new Intent(Login.this, Register.class);

        signin_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(sintent);
            }
        });

        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // show password
//                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                } else {
//                    // hide password
//                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                }
//            }
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (edtPassword != null) {
                    if (isChecked) {
                        // show password
                        edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        // hide password
                        edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                } else {
                    Log.e("Login", "edtPassword is null");
                }
            }

        });



        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.etEmail.getText().toString().isEmpty()){
                    binding.etEmail.setError("enter your email");
                    return;
                }

                if(binding.etPassword.getText().toString().isEmpty()){
                    binding.etPassword.setError("enter your password");
                    return;
                }
                progressDialog.show();
                auth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                      Intent i = new Intent(Login.this, FirstScreen.class);
                                      startActivity(i);
                                } else {
                                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(Login.this, FirstScreen.class);
            startActivity(intent);
        }
    }

    int RC_SIGN_IN = 65;
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
       if(requestCode == RC_SIGN_IN){
           Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
           try{
               // Google sign in was successful, authenticate with firebase
               GoogleSignInAccount account = task.getResult(ApiException.class);
               Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
               firebaseAuthWithGoogle(account.getIdToken());
           } catch (ApiException e){
               // Google sign in failed, update UI  appropriately
               Log.w("TAG", "Google sign in failed", e);
           }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // sign in success update UI with the signed in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilepic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);

                            Intent intent = new Intent(Login.this, FirstScreen.class);
                            startActivity(intent);
                            Toast.makeText(Login.this, "Sign in with Google", Toast.LENGTH_SHORT).show();
                        } else{
                            // if sign In fails display a message to the user
                            Log.w("TAG", "signInWithCredentials:failure", task.getException());
                        }
                    }
                });
        
    }

//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, (@NonNull AuthResult task) -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(MainActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}
}