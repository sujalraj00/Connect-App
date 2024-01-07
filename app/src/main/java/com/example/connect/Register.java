package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.connect.Models.Users;
import com.example.connect.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private FirebaseAuth auth;
EditText etPassword,etEmail;
Spinner course_spinner, year_spinner;
Button registerBtn;
CheckBox checkBox2;
ActivityRegisterBinding binding;
FirebaseDatabase database;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're creating your account");


        // defining

        checkBox2 = findViewById(R.id.checkbox2);
        course_spinner = findViewById(R.id.course_spinner);
        year_spinner = findViewById(R.id.year_spinner);


        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });



        binding.loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent u = new Intent(Register.this, Login.class);
                startActivity(u);
            }
        });

        String [] course ={"Course","B.tech", "MBA", "MCA", "B.Pharma"};
        ArrayAdapter<String> course_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                course);
        //apply the adapter to the spipnner
        course_spinner.setAdapter(course_adapter);


        String [] year = {"year","1st","2nd","3rd","4th","5th"};
        ArrayAdapter<String> year_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                year);
        // apply the adapter to the spinner
        year_spinner.setAdapter(year_adapter);

        // Firebase authentication
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                auth.createUserWithEmailAndPassword
                                (binding.etEmail.getText().toString(), binding.regPassword.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    Users user = new Users(binding.etUserName.getText().toString(), binding.etEmail.getText().toString(),
                                            binding.regPassword.getText().toString());

                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);

                                    Toast.makeText(Register.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }
}