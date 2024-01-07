package com.example.connect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView login_txt;
    Button join_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // defining
        login_txt = findViewById(R.id.login_txt);
        join_btn = findViewById(R.id.join_btn);

        // Intents
        Intent ilogin = new Intent(MainActivity.this, Login.class);
        Intent join = new Intent(MainActivity.this, Register.class);

        login_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ilogin);
            }
        });
        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(join);
            }
        });



    }
}